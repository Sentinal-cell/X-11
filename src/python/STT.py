import sys
from pathlib import Path
import whisper
import warnings

def main():
    warnings.filterwarnings("ignore", category=UserWarning)

    audio_path = sys.argv[1]

    # Load Whisper model (small / medium / large)
    model = whisper.load_model("small")
    result = model.transcribe(
            audio_path,
            fp16=False,                    # CPU-safe
            language="en",                 # remove for auto-detect
            condition_on_previous_text=False
        )
    print(result["text"].strip(), flush=True)

if __name__ == "__main__":
    main()
