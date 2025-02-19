/*
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.chatloggerplus.statemanagement;

import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;

import java.util.Optional;

public class PlayerStateManager {
    private final Client client;
    private Player localPlayer;
    @Setter
    private GameState currentState;
    private int currentWorld;

    public PlayerStateManager(Client client) {
        this.client = client;
        this.currentState = getGameState();
    }

    public void setLocalPlayerInformation(Optional<GameStateChanged> event) {
        // if the game state has changed, update the current state
        if (event.isPresent()) {
            System.out.println("Game state changed from " + currentState + " to " + event.get().getGameState());
            currentState = event.get().getGameState();
        }
        if (currentState == GameState.LOGGED_IN) {
            Player player = client.getLocalPlayer();
            if (player != null) {
                localPlayer = player;
                setCurrentWorld();
            }
        }
        printPlayerState();
    }

    private void setCurrentWorld() {
        if (localPlayer == null) {
            currentWorld = -1;
        }
        currentWorld = client.getWorld();
    }

    public String getLocalPlayerName() {
        if (localPlayer == null) {
            return null;
        }
        return localPlayer.getName();
    }

    public int[] getLocalPlayerPosition() {
        if (localPlayer == null) {
            return new int[]{-1, -1};
        }
        return new int[]{
                localPlayer.getWorldLocation().getX(),
                localPlayer.getWorldLocation().getY()
        };
    }

    private GameState getGameState() {
        return client.getGameState();
    }

    private void printPlayerState() {
        System.out.println("Local player name: " + getLocalPlayerName());
        System.out.println("Local player position: " + getLocalPlayerPosition()[0] + ", " + getLocalPlayerPosition()[1]);
        System.out.println("Current world: " + currentWorld);
    }
}
