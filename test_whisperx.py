
import time
import json
import re
from pathlib import Path

import torch
import whisperx

AUDIO_PATH = "uploads/podcasts/podcast-9.wav"
SCRIPT_PATH = "test_script.txt"
OUTPUT_PATH = "test_alignment.json"

# Mac에서는 우선 CPU로 테스트
DEVICE = "cpu"

# 기존 영어 스크립트 읽기
script = Path(SCRIPT_PATH).read_text(encoding="utf-8").strip()

# 문장 분리
sentences = re.split(r"(?<=[.!?])\s+", script)
sentences = [s.strip() for s in sentences if s.strip()]

print(f"문장 개수: {len(sentences)}")

# 음성 길이 확인
audio = whisperx.load_audio(AUDIO_PATH)

print(f"음성 길이: {len(audio) / 16000:.2f}초")

# WhisperX 정렬 모델 로드
print("정렬 모델 로딩 중...")

load_start = time.perf_counter()

align_model, metadata = whisperx.load_align_model(
    language_code="en",
    device=DEVICE
)

print(
    f"모델 로딩 시간: "
    f"{time.perf_counter() - load_start:.2f}초"
)

# 전체 스크립트를 하나의 구간으로 정렬
segments = [
    {
        "text": script,
        "start": 0.0,
        "end": len(audio) / 16000
    }
]


print("음성-스크립트 정렬 시작...")

start_time = time.perf_counter()

result = whisperx.align(
    segments,
    align_model,
    metadata,
    audio,
    DEVICE,
    return_char_alignments=False
)

elapsed = time.perf_counter() - start_time

print(f"\n정렬 처리 시간: {elapsed:.2f}초")

# 결과 저장
Path(OUTPUT_PATH).write_text(
    json.dumps(
        result,
        ensure_ascii=False,
        indent=2
    ),
    encoding="utf-8"
)

print(f"결과 저장 완료: {OUTPUT_PATH}")

for index, segment in enumerate(result["segments"]):
    print(
        f"[{index}] "
        f"{segment.get('start', 0):.2f}s ~ "
        f"{segment.get('end', 0):.2f}s | "
        f"{segment['text']}"
    )
