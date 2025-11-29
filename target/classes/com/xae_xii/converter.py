# tts_generate
import os
from gtts import gTTS
import sys
import subprocess
text = sys.argv[1]
# project root
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", "..", "..", ".."))

output_folder = os.path.join(project_root, "voice_files")
os.makedirs(output_folder, exist_ok=True)
output_file = os.path.join(output_folder, "output.ogg")

#temporary MP3
temp_mp3 = os.path.join(output_folder, "temp.mp3")
tts = gTTS(text, lang='en')
tts.save(temp_mp3)

#Convert MP3 → OGG/Opus using ffmpeg
subprocess.run([
    "ffmpeg", "-y", "-i", temp_mp3, "-c:a", "libopus", output_file
], check=True)

# Step 3: Clean up temp MP3
os.remove(temp_mp3)

print(f"Saved TTS to {output_file}")
