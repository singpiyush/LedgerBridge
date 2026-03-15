package io.ledgerbridge.service;

import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.ConnectionCreateRequest;
import io.ledgerbridge.model.dto.ConnectionResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.entity.Connection;
import io.ledgerbridge.model.enums.ConnectionProvider;
import io.ledgerbridge.model.enums.ConnectionStatus;
import io.ledgerbridge.model.enums.ConnectionType;
import io.ledgerbridge.repository.ConnectionRepository;
import io.ledgerbridge.util.EntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    public PaginatedResponse<ConnectionResponse> listConnections(String userId, String cursor, int limit) {
        List<Connection> connections;
        if (cursor != null) {
            connections = connectionRepository.findByUserIdAndIdGreaterThanOrderByIdAsc(
                    userId, cursor, PageRequest.of(0, limit + 1));
        } else {
            connections = connectionRepository.findByUserIdOrderByIdAsc(
                    userId, PageRequest.of(0, limit + 1));
        }

        boolean hasMore = connections.size() > limit;
        if (hasMore) {
            connections = connections.subList(0, limit);
        }

        List<ConnectionResponse> data = connections.stream()
                .map(EntityMapper::toConnectionResponse)
                .toList();

        String nextCursor = hasMore ? connections.get(connections.size() - 1).getId() : null;
        long totalCount = connectionRepository.countByUserId(userId);

        return PaginatedResponse.of(data, hasMore, nextCursor, totalCount);
    }

    @Transactional
    public ConnectionResponse createConnection(ConnectionCreateRequest request) {
        ConnectionProvider provider;
        try {
            provider = ConnectionProvider.valueOf(request.provider().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.validation("Invalid provider: " + request.provider(), "provider");
        }

        ConnectionType type;
        try {
            type = ConnectionType.valueOf(request.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.validation("Invalid type: " + request.type(), "type");
        }

        Connection conn = new Connection();
        conn.setUserId(request.userId());
        conn.setProvider(provider);
        conn.setType(type);

        if (type == ConnectionType.EMAIL) {
            if (request.redirectUri() == null) {
                throw ApiException.validation("redirect_uri is required for email connections", "redirect_uri");
            }
            // In production, this would generate a real OAuth URL
            conn.setRedirectUrl("https://oauth.provider.com/authorize?redirect_uri=" + request.redirectUri());
            conn.setStatus(ConnectionStatus.PENDING);
        } else {
            if (request.credentials() == null) {
                throw ApiException.validation("credentials are required for broker connections", "credentials");
            }
            conn.setCredentialsEncrypted(request.credentials().clientId());
            conn.setStatus(ConnectionStatus.ACTIVE);
        }

        conn = connectionRepository.save(conn);
        return EntityMapper.toConnectionResponse(conn);
    }

    public ConnectionResponse getConnection(String connectionId) {
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> ApiException.notFound("connection"));
        return EntityMapper.toConnectionResponse(conn);
    }

    @Transactional
    public void deleteConnection(String connectionId) {
        Connection conn = connectionRepository.findById(connectionId)
                .orElseThrow(() -> ApiException.notFound("connection"));
        conn.setStatus(ConnectionStatus.REVOKED);
        connectionRepository.save(conn);
        connectionRepository.delete(conn);
    }
}
