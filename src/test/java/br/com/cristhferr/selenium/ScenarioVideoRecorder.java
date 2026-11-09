package br.com.cristhferr.selenium;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

final class ScenarioVideoRecorder {
  private static final DateTimeFormatter FILE_TIMESTAMP =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

  private final String scenarioName;
  private final Path outputDirectory;
  private final String ffmpegPath;
  private final String windowTitle;

  private Process process;
  private Path outputFile;
  private Path logFile;

  ScenarioVideoRecorder(String scenarioName) {
    this.scenarioName = scenarioName;
    outputDirectory = Path.of(System.getProperty("video.dir", "target/videos"));
    ffmpegPath = System.getProperty("ffmpeg.path", "ffmpeg");
    windowTitle = System.getProperty("video.windowTitle", "Advantage Shopping");
  }

  void start() {
    if (!System.getProperty("os.name", "").toLowerCase().contains("windows")) {
      throw new IllegalStateException(
          "A gravação de vídeo está configurada para Windows (FFmpeg gdigrab)."
      );
    }

    try {
      Files.createDirectories(outputDirectory);
      String baseName = safeFileName(scenarioName)
          + "-"
          + LocalDateTime.now().format(FILE_TIMESTAMP)
          + "-"
          + UUID.randomUUID().toString().substring(0, 8);
      outputFile = outputDirectory.resolve(baseName + ".mp4");
      logFile = outputDirectory.resolve(baseName + ".ffmpeg.log");

      List<String> command = new ArrayList<>(List.of(
          ffmpegPath,
          "-hide_banner",
          "-loglevel", "error",
          "-nostats",
          "-f", "gdigrab",
          "-framerate", "15",
          "-i", "title=" + windowTitle,
          "-an",
          "-c:v", "libx264",
          "-preset", "ultrafast",
          "-pix_fmt", "yuv420p",
          "-y",
          outputFile.toString()
      ));
      process = new ProcessBuilder(command)
          .redirectErrorStream(true)
          .redirectOutput(logFile.toFile())
          .start();

      if (!process.waitFor(1, TimeUnit.SECONDS)) {
        return;
      }
      throw recorderFailure("O FFmpeg encerrou durante a inicialização.");
    } catch (IOException exception) {
      throw new IllegalStateException(
          "Não foi possível iniciar o FFmpeg. Instale-o ou informe -Dffmpeg.path.",
          exception
      );
    } catch (InterruptedException exception) {
      if (process != null && process.isAlive()) {
        process.destroyForcibly();
      }
      Thread.currentThread().interrupt();
      throw new IllegalStateException(
          "A inicialização da gravação foi interrompida.",
          exception
      );
    }
  }

  void stop() {
    if (process == null) {
      return;
    }

    try {
      if (process.isAlive()) {
        process.getOutputStream().write("q\n".getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().flush();
        if (!process.waitFor(15, TimeUnit.SECONDS)) {
          process.destroy();
          if (!process.waitFor(2, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            process.waitFor();
          }
          throw recorderFailure("O FFmpeg não encerrou a gravação normalmente.");
        }
      }

      if (process.exitValue() != 0) {
        throw recorderFailure("O FFmpeg encerrou com erro.");
      }
      Files.deleteIfExists(logFile);
    } catch (IOException exception) {
      throw new IllegalStateException(
          "Não foi possível finalizar a gravação em " + outputFile + ".",
          exception
      );
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(
          "A finalização da gravação foi interrompida.",
          exception
      );
    } finally {
      process = null;
    }
  }

  private IllegalStateException recorderFailure(String message) {
    String details;
    try {
      details = Files.exists(logFile) ? Files.readString(logFile).trim() : "";
    } catch (IOException exception) {
      details = "Não foi possível ler o log do FFmpeg: " + exception.getMessage();
    }
    return new IllegalStateException(
        message + " Log: " + logFile + ". Saída: " + details
    );
  }

  private String safeFileName(String value) {
    String safeName = value.replaceAll("[^a-zA-Z0-9._-]+", "-")
        .replaceAll("^-|-$", "");
    return safeName.isBlank() ? "scenario" : safeName;
  }
}
