/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.loader.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameProviders {
	private static List<GameProvider> providers = new ArrayList();

	private GameProviders() { }

	public static void addProvider(GameProvider p) { providers.add(p); }

	public static List<GameProvider> create() {
		addProvider(new MinecraftGameProvider());
		return providers;
	}
}
