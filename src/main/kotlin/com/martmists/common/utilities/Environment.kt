package com.martmists.common.utilities

import com.jcabi.manifests.Manifests

object Environment {
    val gameType by lazy {
        Manifests.read("Game-Type")
    }

    val gameVersion by lazy {
        Manifests.read("Game-Version")
    }
}
