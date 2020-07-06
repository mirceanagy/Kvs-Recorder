package cli;

import com.amazonaws.kinesisvideo.demoapp.auth.AuthHelper;
import com.amazonaws.services.kinesisvideo.*;
import com.amazonaws.services.kinesisvideo.model.AckEvent;
import com.amazonaws.services.kinesisvideo.model.FragmentTimecodeType;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.PutMediaRequest;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class UploadFileToKVS {

//    private static final String MKV_FILE_PATH = "C:/dev/workspace/forked-kvs-java-demo/src/main/resources/data/mkv/clusters.mkv";
    private static final String MKV_FILE_PATH = "C:/files/test-out.mkv";
//    private static final String MKV_FILE_PATH = "C:/files/test-1593782448.mkv";

    /* connect timeout */
    private static final int CONNECTION_TIMEOUT_IN_MILLIS = 10_000;
    private static final String STREAM = "mnagy-fin-kvs";

    private static final String DEFAULT_REGION = "us-west-2";
    private static final String PUT_MEDIA_API = "/putMedia";

    public static void main(String[] args) {
        try {
//            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//                Path tmpPath = Paths.get(MKV_FILE_PATH + "tmp.mkv");
//                FFmpeg.atPath(Paths.get("C:/dev/ffmpeg-20200628-4cfcfb3-win64-static/bin"))
//                        .addInput(UrlInput.fromPath(Paths.get(MKV_FILE_PATH)))
//                        .addOutput(UrlOutput.toPath(tmpPath)/*.setFormat("mkv")*/)
//                        .setOverwriteOutput(true)
//                        .execute();
//                FFmpeg.atPath(Paths.get("C:/dev/ffmpeg-20200628-4cfcfb3-win64-static/bin"))
//                        .addInput(PipeInput.pumpFrom(new FileInputStream(MKV_FILE_PATH)))
//                        .addOutput(PipeOutput.pumpTo(outputStream))
//                        .setOverwriteOutput(true)
//                        .execute();
//                store(new FileInputStream(tmpPath.toFile()));

//            }
            store(new FileInputStream(new File(MKV_FILE_PATH)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void store(InputStream is) {
        final AmazonKinesisVideo frontendClient = AmazonKinesisVideoAsyncClient.builder()
                .withCredentials(AuthHelper.getSystemPropertiesCredentialsProvider())
                .withRegion(DEFAULT_REGION)
                .build();

        /* this is the endpoint returned by GetDataEndpoint API */
        String dataEndpoint = frontendClient.getDataEndpoint(
                new GetDataEndpointRequest()
                        .withStreamName(STREAM)
                        .withAPIName("PUT_MEDIA")).getDataEndpoint();

        AmazonKinesisVideoPutMedia dataClient = AmazonKinesisVideoPutMediaClient.builder()
                .withRegion(DEFAULT_REGION)
                .withEndpoint(URI.create(dataEndpoint))
                .withCredentials(AuthHelper.getSystemPropertiesCredentialsProvider())
                .withConnectionTimeoutInMillis(CONNECTION_TIMEOUT_IN_MILLIS)
                .build();
        try {
            while (true) {
                final CountDownLatch latch = new CountDownLatch(1);

                final PutMediaAckResponseHandler responseHandler = new PutMediaAckResponseHandler() {
                    @Override
                    public void onAckEvent(AckEvent event) {
                        System.out.println("onAckEvent " + event);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println("onFailure: " + t.getMessage());
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                };

                /* start streaming video in a background thread */
                dataClient.putMedia(new PutMediaRequest()
                                .withStreamName(STREAM)
                                .withFragmentTimecodeType(FragmentTimecodeType.ABSOLUTE)
                                .withPayload(is)
                                .withProducerStartTimestamp(Date.from(Instant.now())),
                        responseHandler);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            dataClient.close();
        }
    }

}
