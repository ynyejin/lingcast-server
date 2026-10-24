
package com.lingcast.server.domain.podcast.client;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class WhisperXClient {

    private final ObjectMapper objectMapper;

    public WhisperXClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<AlignedSentence> align(
            String audioPath,
            String scriptPath
    ) {
        try {
            Path projectDir = Path.of("")
                    .toAbsolutePath()
                    .normalize();

            Path pythonPath = projectDir.resolve(
                    ".venv-whisperx/bin/python"
            );

            Path alignScriptPath = projectDir.resolve(
                    "scripts/align_podcast.py"
            );

            Path wavPath = projectDir.resolve(audioPath);
            Path textPath = projectDir.resolve(scriptPath);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonPath.toString(),
                    alignScriptPath.toString(),
                    wavPath.toString(),
                    textPath.toString()
            );

            processBuilder.directory(projectDir.toFile());

            // Python의 오류 메시지는 별도 파일로 저장
            Path errorLog = java.nio.file.Files.createTempFile(
                    "whisperx-error-",
                    ".log"
            );

            try {
                processBuilder.redirectError(errorLog.toFile());

                Process process = processBuilder.start();

                // Python 표준 출력을 읽음
                String output;
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(
                                process.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                )) {
                    output = reader.lines()
                            .reduce("", (a, b) -> a + b);
                }

                boolean finished = process.waitFor(
                        Duration.ofMinutes(5).toMillis(),
                        TimeUnit.MILLISECONDS
                );

                if (!finished) {
                    process.destroyForcibly();
                    throw new IllegalStateException(
                            "WhisperX 실행 시간이 초과되었습니다."
                    );
                }

                if (process.exitValue() != 0) {
                    String error = java.nio.file.Files.readString(
                            errorLog
                    );

                    throw new IllegalStateException(
                            "WhisperX 실행 실패: " + error
                    );
                }

                List<AlignedSentence> sentences =
                        objectMapper.readValue(
                                output,
                                new TypeReference<List<AlignedSentence>>() {}
                        );

                if (sentences.isEmpty()) {
                    throw new IllegalStateException(
                            "WhisperX 정렬 결과가 비어 있습니다."
                    );
                }

                return sentences;

            } finally {
                java.nio.file.Files.deleteIfExists(errorLog);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "WhisperX 실행 또는 결과 처리 실패",
                    e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "WhisperX 실행이 중단되었습니다.",
                    e
            );
        }
    }

    public record AlignedSentence(
            Integer sentenceIndex,
            String english,
            Long startMs,
            Long endMs
    ) {}
}
