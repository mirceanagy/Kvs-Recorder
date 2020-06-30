package com.devfactory.recorder.services;

import com.amazonaws.kinesisvideo.demoapp.auth.AuthHelper;
import com.amazonaws.services.kinesisvideo.*;
import com.amazonaws.services.kinesisvideo.model.AckEvent;
import com.amazonaws.services.kinesisvideo.model.FragmentTimecodeType;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.PutMediaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.Date;

@Service
public class KvsStorageService {

    @Value("${kvsStream}")
    private String kvsStream;

    /* connect timeout */
    private static final int CONNECTION_TIMEOUT_IN_MILLIS = 10_000;

    private static final String DEFAULT_REGION = "us-west-2";
    private static final String PUT_MEDIA_API = "/putMedia";

    AmazonKinesisVideoPutMedia dataClient;

    @PostConstruct
    public void initialize() {
        final AmazonKinesisVideo frontendClient = AmazonKinesisVideoAsyncClient.builder()
                .withCredentials(AuthHelper.getSystemPropertiesCredentialsProvider())
                .withRegion(DEFAULT_REGION)
                .build();

        /* this is the endpoint returned by GetDataEndpoint API */
        String dataEndpoint = frontendClient.getDataEndpoint(
                new GetDataEndpointRequest()
                        .withStreamName(kvsStream)
                        .withAPIName("PUT_MEDIA")).getDataEndpoint();

        dataClient = AmazonKinesisVideoPutMediaClient.builder()
                .withRegion(DEFAULT_REGION)
                .withEndpoint(URI.create(dataEndpoint))
                .withCredentials(AuthHelper.getSystemPropertiesCredentialsProvider())
                .withConnectionTimeoutInMillis(CONNECTION_TIMEOUT_IN_MILLIS)
                .build();
    }

    public void store(String fileName, InputStream is) {
        final PutMediaAckResponseHandler responseHandler = new PutMediaAckResponseHandler() {
            @Override
            public void onAckEvent(AckEvent event) {
                System.out.println("onAckEvent " + event);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("onFailure: " + t.getMessage());
            }

            @Override
            public void onComplete() {
            }
        };

        /* start streaming video in a background thread */
        dataClient.putMedia(new PutMediaRequest()
                        .withStreamName(kvsStream)
                        .withFragmentTimecodeType(FragmentTimecodeType.ABSOLUTE)
                        .withPayload(is)
                        .withProducerStartTimestamp(Date.from(Instant.now())),
                responseHandler);
    }

    @PreDestroy
    public void preDestroy() {
        dataClient.close();
    }
}
