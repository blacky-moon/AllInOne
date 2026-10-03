package org.blacky_moon.hexcurio.common.inits

import net.neoforged.bus.api.IEventBus

sealed class InitHook{abstract fun init(bus: IEventBus)}