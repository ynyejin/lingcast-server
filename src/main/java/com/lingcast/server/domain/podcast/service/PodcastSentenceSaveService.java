
package com.lingcast.server.domain.podcast.service;

import com.lingcast.server.domain.podcast.client.WhisperXClient;
import com.lingcast.server.domain.podcast.entity.Podcast;
import com.lingcast.server.domain.podcast.entity.PodcastSentence;
import com.lingcast.server.domain.podcast.repository.PodcastRepository;
import com.lingcast.server.domain.podcast.repository.PodcastSentenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PodcastSentenceSaveService {

    private final PodcastRepository podcastRepository;
    private final PodcastSentenceRepository podcastSentenceRepository;

    @Transactional
    public void save(
            Long podcastId,
            List<WhisperXClient.AlignedSentence> aligned
    ) {

        Podcast podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 팟캐스트입니다."
                        )
                );

        // 중복 저장 방지
        if (!podcastSentenceRepository
                .findByPodcastIdOrderBySentenceIndexAsc(podcastId)
                .isEmpty()) {
            return;
        }

        if (aligned == null || aligned.isEmpty()) {
            throw new IllegalArgumentException(
                    "저장할 자막 데이터가 없습니다."
            );
        }

        List<PodcastSentence> sentences = new ArrayList<>();

        long previousStartMs = -1;

        for (int i = 0; i < aligned.size(); i++) {

            WhisperXClient.AlignedSentence item = aligned.get(i);

            if (item.sentenceIndex() == null ||
                    item.sentenceIndex() != i ||
                    item.english() == null ||
                    item.english().isBlank() ||
                    item.startMs() == null ||
                    item.endMs() == null ||
                    item.startMs() < 0 ||
                    item.startMs() >= item.endMs() ||
                    item.startMs() < previousStartMs) {

                throw new IllegalArgumentException(
                        "잘못된 자막 데이터입니다. index=" + i
                );
            }

            PodcastSentence sentence = PodcastSentence.create(
                    podcast,
                    item.sentenceIndex(),
                    item.english(),
                    null,
                    item.startMs(),
                    item.endMs()
            );

            sentences.add(sentence);
            previousStartMs = item.startMs();
        }

        podcastSentenceRepository.saveAll(sentences);
    }
}
