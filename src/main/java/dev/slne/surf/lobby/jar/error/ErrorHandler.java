package dev.slne.surf.lobby.jar.error;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ErrorHandler {
  public static final ErrorHandler instance = new ErrorHandler();
  private final Object2ObjectMap<String, ParkourError> errors = new Object2ObjectOpenHashMap<>();

  public ParkourError getError(String identifier){
    return ParkourError.builder().build();
  }
}
