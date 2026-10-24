
package com.lingcast.server.domain.podcast.service;

import com.lingcast.server.domain.podcast.client.WhisperXClient;
import com.lingcast.server.domain.podcast.entity.Podcast;
import com.lingcast.server.domain.podcast.entity.PodcastSentence;
import com.lingcast.server.domain.podcast.repository.PodcastRepository;
import com.lingcast.server.domain.podcast.repository.PodcastSentenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PodcastAlignmentService {

    private final WhisperXClient whisperXClient;
    private final PodcastRepository podcastRepository;
    private final PodcastSentenceRepository podcastSentenceRepository;
    private final PodcastSentenceSaveService sentenceSaveService;

    @Async("podcastAlignmentExecutor")
    public void alignAsync(Long podcastId) {

        Path scriptPath = null;

        try {
            Podcast podcast = podcastRepository.findById(podcastId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "존재하지 않는 팟캐스트입니다."
                            )
                    );

            // 이미 자막이 저장된 경우 중복 실행 방지
            if (!podcastSentenceRepository
                    .findByPodcastIdOrderBySentenceIndexAsc(podcastId)
                    .isEmpty()) {
                log.info("이미 자막이 존재합니다. podcastId={}", podcastId);
                return;
            }

            Path projectDir = Path.of("")
                    .toAbsolutePath()
                    .normalize();

            // DB에 저장된 상대 오디오 URL을 로컬 경로로 변환
            String audioUrl = podcast.getAudioUrl();

            if (audioUrl == null ||
                    !audioUrl.startsWith("/audio/podcasts/")) {
                throw new IllegalStateException(
                        "지원하지 않는 오디오 경로: " + audioUrl
                );
            }

            String fileName = audioUrl.substring(
                    "/audio/podcasts/".length()
            );

            Path audioPath = projectDir
                    .resolve("uploads/podcasts")
                    .resolve(fileName)
                    .normalize();

            // 경로가 업로드 폴더 밖으로 벗어나지 않도록 검사
            Path uploadsDir = projectDir
                    .resolve("uploads/podcasts")
                    .normalize();

            if (!audioPath.startsWith(uploadsDir)) {
                throw new IllegalStateException(
                        "잘못된 오디오 파일 경로입니다."
                );
            }

            // 임시 영어 스크립트 파일 생성
            scriptPath = Files.createTempFile(
                    "lingcast-script-",
                    ".txt"
            );

            Files.writeString(
                    scriptPath,
                    podcast.getScript(),
                    StandardCharsets.UTF_8
            );

            log.info("WhisperX 자막 분석 시작. podcastId={}", podcastId);

            List<WhisperXClient.AlignedSentence> aligned =
                    whisperXClient.align(
                            audioPath.toString(),
                            scriptPath.toString()
                    );

            // DB 저장은 별도 트랜잭션에서 처리
            sentenceSaveService.save(podcastId, aligned);

            log.info(
                    "WhisperX 자막 저장 완료. podcastId={}, sentences={}",
                    podcastId,
                    aligned.size()
            );

        } catch (Exception e) {
            // 자막 분석 실패가 음성 재생을 방해하지 않도록 처리
            log.error(
                    "WhisperX 자막 분석 실패. podcastId={}",
                    podcastId,
                    e
            );

        } finally {
            if (scriptPath != null) {
                try {
                    Files.deleteIfExists(scriptPath);
                } catch (Exception e) {
                    log.warn("임시 스크립트 파일 삭제 실패", e);
                }
            }
        }
    }
}
