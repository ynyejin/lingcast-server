
import sys
import json
import re
from pathlib import Path

import whisperx


def align_podcast(audio_path, script_path):

    # 영어 스크립트 읽기
    script = Path(script_path).read_text(
        encoding="utf-8"
    ).strip()

    if not script:
        raise ValueError("스크립트가 비어 있습니다.")

    # 문장 분리
    sentences = re.split(
        r"(?<=[.!?])\s+",
        script
    )

    sentences = [
        sentence.strip()
        for sentence in sentences
        if sentence.strip()
    ]

    # WAV 파일 로드
    audio = whisperx.load_audio(audio_path)
    duration = len(audio) / 16000

    # 영어 정렬 모델 로드
    model, metadata = whisperx.load_align_model(
        language_code="en",
        device="cpu"
    )

    # 전체 스크립트를 하나의 구간으로 정렬
    segments = [
        {
            "text": script,
            "start": 0.0,
            "end": duration
        }
    ]

    result = whisperx.align(
        segments,
        model,
        metadata,
        audio,
        "cpu",
        return_char_alignments=False
    )

    # 문장별 타임스탬프 반환
    aligned_sentences = []

    for index, segment in enumerate(result["segments"]):

        aligned_sentences.append({
            "sentenceIndex": index,
            "english": segment["text"].strip(),
            "startMs": round(segment["start"] * 1000),
            "endMs": round(segment["end"] * 1000)
        })

    return aligned_sentences


if __name__ == "__main__":

    if len(sys.argv) != 3:
        raise ValueError(
            "사용법: python align_podcast.py "
            "<audio_path> <script_path>"
        )

    result = align_podcast(
        sys.argv[1],
        sys.argv[2]
    )

    # Spring Boot가 읽을 JSON 출력
    print(json.dumps(
        result,
        ensure_ascii=False
    ))
