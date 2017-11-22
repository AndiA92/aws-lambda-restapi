package io.github.AndiA92.aws.lambda.restapi.exceptions;

public class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException(String message) {
        super(message);
    }
}
