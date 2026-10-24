
package com.lingcast.server.domain.podcast.client;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

class WhisperXClientTest {

    @Test
    void alignPodcast() {

        WhisperXClient client =
                new WhisperXClient(new ObjectMapper());

        List<WhisperXClient.AlignedSentence> result =
                client.align(
                        "uploads/podcasts/podcast-9.wav",
                        "test_script.txt"
                );

        assertEquals(8, result.size());

        for (WhisperXClient.AlignedSentence sentence : result) {

            assertNotNull(sentence.sentenceIndex());
            assertNotNull(sentence.english());
            assertNotNull(sentence.startMs());
            assertNotNull(sentence.endMs());

            org.junit.jupiter.api.Assertions.assertTrue(
                    sentence.startMs() < sentence.endMs()
            );

            System.out.printf(
                    "[%d] %dms ~ %dms | %s%n",
                    sentence.sentenceIndex(),
                    sentence.startMs(),
                    sentence.endMs(),
                    sentence.english()
            );
        }
    }
}
