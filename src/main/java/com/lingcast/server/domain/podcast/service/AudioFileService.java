package com.lingcast.server.domain.podcast.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AudioFileService {

    private static final int SAMPLE_RATE = 24000;
    private static final int CHANNELS = 1;
    private static final int BITS_PER_SAMPLE = 16;

    public String saveAsWav(Long podcastId, byte[] pcmData) {

        try {
            // PCM 데이터를 WAV 형식으로 변환
            byte[] wavData = addWavHeader(pcmData);

            // 팟캐스트 음성 파일 저장 폴더 생성
            Path directory = Paths.get("uploads", "podcasts");
            Files.createDirectories(directory);

            Path filePath = directory.resolve(
                    "podcast-" + podcastId + ".wav"
            );

            Files.write(filePath, wavData);

            // 클라이언트에서 사용할 음성 파일 URL 반환
            return "/audio/podcasts/" + filePath.getFileName();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "팟캐스트 음성 파일 저장에 실패했습니다.",
                    e
            );
        }
    }

    private byte[] addWavHeader(byte[] pcmData) {

        int byteRate =
                SAMPLE_RATE * CHANNELS * BITS_PER_SAMPLE / 8;

        int totalDataLength = pcmData.length + 36;

        byte[] header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        writeInt(header, 4, totalDataLength);

        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        writeInt(header, 16, 16);

        header[20] = 1;
        header[21] = 0;

        header[22] = (byte) CHANNELS;
        header[23] = 0;

        writeInt(header, 24, SAMPLE_RATE);
        writeInt(header, 28, byteRate);

        header[32] =
                (byte) (CHANNELS * BITS_PER_SAMPLE / 8);
        header[33] = 0;

        header[34] = (byte) BITS_PER_SAMPLE;
        header[35] = 0;

        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        writeInt(header, 40, pcmData.length);

        byte[] wavData =
                new byte[header.length + pcmData.length];

        System.arraycopy(
                header, 0,
                wavData, 0,
                header.length
        );

        System.arraycopy(
                pcmData, 0,
                wavData, header.length,
                pcmData.length
        );

        return wavData;
    }

    private void writeInt(
            byte[] data,
            int offset,
            int value
    ) {
        data[offset] = (byte) value;
        data[offset + 1] = (byte) (value >> 8);
        data[offset + 2] = (byte) (value >> 16);
        data[offset + 3] = (byte) (value >> 24);
    }
}