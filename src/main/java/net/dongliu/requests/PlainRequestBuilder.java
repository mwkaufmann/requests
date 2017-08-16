package net.dongliu.requests;

import java.util.Objects;

/**
 * The Http Request builder for Client.
 *
 * @author Liu Dong
 */
public final class PlainRequestBuilder extends BaseRequestBuilder<PlainRequestBuilder> {
    private Client client;

    PlainRequestBuilder(Client client) {
        super();
        this.client = Objects.requireNonNull(client);
    }

    PlainRequestBuilder(Client client, Request request) {
        super(request);
        this.client = Objects.requireNonNull(client);
    }

    /**
     * Build http request, and send out. This method exists for fluent api
     */
    @Override
    public RawResponse send() {
        Request request = build();
        return client.execute(request);
    }
}