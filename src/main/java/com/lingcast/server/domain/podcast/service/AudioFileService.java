
package com.lingcast.server.domain.podcast.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class AudioFileService {

    private static final int SAMPLE_RATE = 24000;
    private static final int CHANNELS = 1;
    private static final int BITS_PER_SAMPLE = 16;

    private static final int BYTES_PER_SAMPLE =
            CHANNELS * BITS_PER_SAMPLE / 8;

    // 기존 PCM -> WAV 저장 기능
    public String saveAsWav(Long podcastId, byte[] pcmData) {
        try {
            byte[] wavData = addWavHeader(pcmData);

            Path directory = Paths.get("uploads", "podcasts");
            Files.createDirectories(directory);

            Path filePath = directory.resolve(
                    "podcast-" + podcastId + ".wav"
            );

            Files.write(filePath, wavData);

            return "/audio/podcasts/" + filePath.getFileName();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "팟캐스트 음성 파일 저장에 실패했습니다.",
                    e
            );
        }
    }

    // PCM 음성의 길이를 밀리초로 계산
    public long calculateDurationMs(byte[] pcmData) {
        if (pcmData == null || pcmData.length == 0) {
            return 0;
        }

        long bytesPerSecond =
                (long) SAMPLE_RATE * BYTES_PER_SAMPLE;

        return pcmData.length * 1000L / bytesPerSecond;
    }

    // 여러 문장의 PCM 데이터를 하나로 합치기
    public byte[] mergePcmAudio(List<byte[]> audioSegments) {
        if (audioSegments == null || audioSegments.isEmpty()) {
            throw new IllegalArgumentException(
                    "합칠 음성 데이터가 없습니다."
            );
        }

        try {
            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            for (byte[] segment : audioSegments) {
                if (segment == null || segment.length == 0) {
                    throw new IllegalArgumentException(
                            "비어 있는 음성 데이터가 포함되어 있습니다."
                    );
                }

                if (segment.length % BYTES_PER_SAMPLE != 0) {
                    throw new IllegalArgumentException(
                            "PCM 데이터의 샘플 크기가 올바르지 않습니다."
                    );
                }

                output.write(segment);
            }

            return output.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "PCM 음성 병합에 실패했습니다.",
                    e
            );
        }
    }

    // PCM 데이터를 WAV 형식으로 변환
    private byte[] addWavHeader(byte[] pcmData) {
        if (pcmData == null || pcmData.length == 0) {
            throw new IllegalArgumentException(
                    "PCM 음성 데이터가 비어 있습니다."
            );
        }

        if (pcmData.length % BYTES_PER_SAMPLE != 0) {
            throw new IllegalArgumentException(
                    "PCM 데이터의 샘플 크기가 올바르지 않습니다."
            );
        }

        int byteRate =
                SAMPLE_RATE * CHANNELS * BITS_PER_SAMPLE / 8;

        long riffSize = (long) pcmData.length + 36;

        if (riffSize > 0xFFFFFFFFL) {
            throw new IllegalArgumentException(
                    "WAV 파일의 최대 크기를 초과했습니다."
            );
        }

        byte[] header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        writeInt(header, 4, (int) riffSize);

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

        header[32] = (byte) BYTES_PER_SAMPLE;
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
