package com.isycat.recog;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionAsync;
import com.amazonaws.services.rekognition.AmazonRekognitionAsyncClientBuilder;
import com.amazonaws.services.rekognition.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class FaceDetectionTest {
    private static final AmazonRekognitionAsync REKOG = AmazonRekognitionAsyncClientBuilder
            .standard()
            .withRegion(Regions.EU_WEST_1)
            .withCredentials(new CredProvider())
            .build();

    public static void doThings() throws Exception {
        // 7a59cb2fbaeed9f362d9b36d5d4e0c2265542c7ff2cf5ac621834141af2c1a24

//        final StartFaceDetectionResult result = startFaceDetection(
//                new NotificationChannel()
//                        .withSNSTopicArn("arn:aws:sns:eu-west-1:762170374546:rekog-topic-1")
//                        .withRoleArn("arn:aws:iam::762170374546:role/RekognitionRole"));
//        System.out.println(result.getJobId());

        // todo: result.getJobId()
        final String jobId = "7a59cb2fbaeed9f362d9b36d5d4e0c2265542c7ff2cf5ac621834141af2c1a24";

        GetFaceDetectionResult detectionResult;
        do {
            detectionResult = REKOG.getFaceDetection(
                    new GetFaceDetectionRequest()
                            .withJobId(jobId));
            System.out.println(detectionResult.toString());
        } while ("IN_PROGRESS".equals(detectionResult.getJobStatus()));


        final Properties aggregateEmotions = new Properties();

        detectionResult.getFaces()
                .forEach(face -> {
                    face.getFace().getEmotions().forEach(emotion -> {
                        // ms per frame = 200
                        // values returned as % * 100, so: x / 100 * 200 = x * 2
                        double newValue = Double.parseDouble(
                                (String) aggregateEmotions.getOrDefault(emotion.getType(), "0"))
                                + emotion.getConfidence() * 2;
                        aggregateEmotions.setProperty(emotion.getType(), "" + newValue);
                    });
                });

        System.out.println("aggregate");
        System.out.println(aggregateEmotions.toString());
    }

    private static StartFaceDetectionResult startFaceDetection(final NotificationChannel notificationChannel) {
        final StartFaceDetectionRequest request = new StartFaceDetectionRequest()
                .withFaceAttributes(FaceAttributes.ALL)
                .withNotificationChannel(notificationChannel)
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                                .withBucket("isycat-rekog-test-1")
                                .withName("rekog-test-linus.mp4")));
        return REKOG.startFaceDetection(request);
    }

    private static void writeResults(final Object results) throws IOException {
        final File testReults = new File("e:\\rekog-test-results.txt");
        try (final FileWriter fileWriter = new FileWriter(testReults)) {
            fileWriter.append(results.toString());
        }
    }
}
