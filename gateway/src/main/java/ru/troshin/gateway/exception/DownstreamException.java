package ru.troshin.gateway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class DownstreamException extends RuntimeException {
    private final String serviceName;
    private final Integer downstreamStatus;
    private final String downstreamBody;

    public DownstreamException(String serviceName, Integer downstreamStatus, String downstreamBody,
                               String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.downstreamStatus = downstreamStatus;
        this.downstreamBody = downstreamBody;
    }

}
