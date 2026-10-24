
package com.lingcast.server.domain.podcast.service;

import org.springframework.stereotype.Component;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class PodcastScriptSplitter {

    public List<String> split(String script) {
        if (script == null || script.isBlank()) {
            throw new IllegalArgumentException(
                    "팟캐스트 스크립트가 비어 있습니다."
            );
        }

        BreakIterator iterator =
                BreakIterator.getSentenceInstance(Locale.ENGLISH);

        iterator.setText(script);

        List<String> sentences = new ArrayList<>();

        int start = iterator.first();

        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {

            String sentence = script
                    .substring(start, end)
                    .trim();

            if (!sentence.isBlank()) {
                sentences.add(sentence);
            }
        }

        if (sentences.isEmpty()) {
            throw new IllegalStateException(
                    "스크립트에서 문장을 추출하지 못했습니다."
            );
        }

        return sentences;
    }
}
