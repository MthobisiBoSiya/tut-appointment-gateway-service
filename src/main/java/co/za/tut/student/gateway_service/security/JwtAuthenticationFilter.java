package co.za.tut.student.gateway_service.security;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();

        // PUBLIC ENDPOINTS
        if (path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/auth/refresh")) {

            return chain.filter(exchange);
        }

        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.extractClaims(token);

        String userId =
                claims.get("userId", String.class);

        String role =
                claims.get("role", String.class);

        String email =
                claims.get("email", String.class);

        ServerHttpRequest request =
                exchange.getRequest()
                        .mutate()
                        .header("X-User-Id", userId)
                        .header("X-Role", role)
                        .header("X-Email", email)
                        .build();

        return chain.filter(
                exchange.mutate()
                        .request(request)
                        .build()
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
