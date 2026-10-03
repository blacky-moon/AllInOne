# Полный гайд по миграции Hexical с Fabric 1.20.1 на NeoForge 1.21.1

Этот гайд составлен на основе реального процесса портирования мода **Hexical** и охватывает все критические изменения API Minecraft, NeoForge и Hex Casting.

---

## 1. Маппинги и Пакеты (Yarn → Mojang)

NeoForge использует официальные маппинги Mojang. Это самое массовое изменение, затрагивающее каждый файл.

| Fabric / Yarn (1.20.1)           | NeoForge / Mojang (1.21.1)                  | Примечание                          |
| :------------------------------- | :------------------------------------------ | :---------------------------------- |
| `net.minecraft.entity.*`         | `net.minecraft.world.entity.*`              | Все сущности                        |
| `net.minecraft.block.*`          | `net.minecraft.world.level.block.*`         | Блоки                               |
| `net.minecraft.item.*`           | `net.minecraft.world.item.*`                | Предметы                            |
| `net.minecraft.util.math.*`      | `net.minecraft.core.*`, `world.phys.*`      | BlockPos, Vec3, Direction           |
| `net.minecraft.world.World`      | `net.minecraft.world.level.Level`           | Мир                                 |
| `ServerWorld`                    | `ServerLevel`                               | Серверный мир                       |
| `PlayerEntity`                   | `Player`                                    | Игрок                               |
| `LivingEntity`                   | `net.minecraft.world.entity.LivingEntity`   | Живая сущность                      |
| `Vec3d`                          | `Vec3`                                      | Вектор                              |
| `Box`                            | `AABB`                                      | Границы                             |
| `NbtCompound`                    | `CompoundTag`                               | NBT                                 |
| `SoundCategory`                  | `SoundSource`                               | Звуки                               |
| `RegistryKey<T>`                 | `ResourceKey<T>`                            | Ключи реестров                      |
| `ItemStack.isOf()`               | `ItemStack.is()`                            | Проверка предмета                   |
| `ItemStack.increment()`          | `ItemStack.grow()`                          | Увеличение количества               |
| `Entity.getEyePos()`             | `Entity.getEyePosition()`                   | Позиция глаз                        |
| `Entity.velocity`                | `Entity.getDeltaMovement()`                 | Скорость/движение                   |
| `Entity.setVelocity()`           | `Entity.setDeltaMovement()` / `push()`      | Установка скорости                  |
| `World.spawnEntity()`            | `Level.addFreshEntity()`                    | Спавн сущности                      |
| `BlockEntityProvider`            | `EntityBlock`                               | Интерфейс блока с сущностью         |
| `Waterloggable`                  | `SimpleWaterloggedBlock`                    | Водонепроницаемость                 |

---

## 2. Система регистрации (DeferredRegister)

Вместо прямого `Registry.register()` используется ленивая регистрация через `DeferredRegister`.

```kotlin
// ❌ Было (Fabric):
val SPIKE_ENTITY = Registry.register(Registries.ENTITY_TYPE, id("spike"), 
    EntityType.Builder.create(::SpikeEntity, SpawnGroup.MISC).build())

// ✅ Стало (NeoForge 1.21.1):
val ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID)

val SPIKE_ENTITY = ENTITY_TYPES.register("spike") {
    EntityType.Builder.of(::SpikeEntity, MobCategory.MISC)
        .sized(1f, 1f)
        .clientTrackingRange(10)
        .updateInterval(1)
        .build()
}

// В главном классе мода:
ENTITY_TYPES.register(modEventBus)

// Получение значения (ВАЖНО!):
SPIKE_ENTITY.value() // или .get()
```

> ⚠️ **Критично:** При передаче `EntityType` в конструкторы или методы регистрации всегда вызывайте `.value()` / `.get()`. Передача самого `DeferredHolder` вызовет ошибку приведения типов.

---

## 3. Data Components (Замена NBT у предметов)

Самое важное изменение 1.20.5+. У `ItemStack` **больше нет** методов `getNbt()`, `hasNbt()`, `getOrCreateNbt()`.

### Чтение кастомных данных
```kotlin
// ❌ Было:
stack.getNbt()?.contains("key")
stack.getNbt()?.getIntArray("needle")

// ✅ Стало:
val customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
val tag = customData.copyTag()
tag.contains("key")
tag.getIntArray("needle")
```

### Запись кастомных данных
```kotlin
// ❌ Было:
stack.getOrCreateNbt().putInt("key", value)

// ✅ Стало:
val tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
tag.putInt("key", value)
stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
```

### Регистрация собственных компонентов
```kotlin
val DATA_COMPONENTS = DeferredRegister.createDataComponents(
    Registries.DATA_COMPONENT_TYPE, MOD_ID
)

val MEDIA_HOLDER = DATA_COMPONENTS.registerComponentType<Long>("media_holder") { builder ->
    builder.persistent(Codec.LONG)
        .networkSynchronized(ByteBufCodecs.VAR_LONG)
}
```

---

## 4. Сущности (Entity)

### SynchedEntityData (DataTracker)
Система синхронизации данных полностью переработана.

```kotlin
// ❌ Было (Fabric):
override fun initDataTracker() {
    dataTracker.startTracking(TIMER, 0)
}
companion object {
    val TIMER = DataTracker.registerData(MyEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
}

// ✅ Стало (NeoForge 1.21.1):
override fun defineSynchedData(builder: SynchedEntityData.Builder) {
    builder.define(TIMER, 0)
}
companion object {
    val TIMER: EntityDataAccessor<Int> = 
        SynchedEntityData.defineId(MyEntity::class.java, EntityDataSerializers.INT)
}
```

### Прочие изменения сущностей
-   `EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES` → хардкод `(byte) 35`
-   `world.sendEntityStatus()` → `level().broadcastEntityEvent()`
-   `ItemStackParticleEffect` → `ItemParticleOption`
-   `setNeverDespawn()` → `setUnlimitedLifetime()`
-   `PersistentProjectileEntity` → `AbstractArrow` (для снарядов)
-   `inGround` → `isGrounded`
-   `age` → `tickCount`
-   `world.damageSources.create()` → `level().damageSources().source()` (обратите внимание: **`source`**, не `create`)

---

## 5. BlockEntity

### Порядок аргументов NBT
В 1.21+ методы сохранения/загрузки требуют `HolderLookup.Provider` для сериализации компонентов. **Порядок аргументов важен!**

```kotlin
// ❌ Неправильный порядок:
override fun loadAdditional(level: HolderLookup.Provider, nbt: CompoundTag)

// ✅ Правильный порядок:
override fun loadAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
    super.loadAdditional(nbt, registries)
    heldStack = ItemStack.parseOptional(registries, nbt.getCompound("item"))
}

override fun saveAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
    super.saveAdditional(nbt, registries)
    nbt.put("item", heldStack.save(registries, CompoundTag()))
}
```

### Container (Inventory)
-   `canPlayerUse()` → `stillValid()`
-   `removeItem(slot)` (без количества) → `removeItemNoUpdate(slot)`
-   `ScreenHandler.calculateComparatorOutput()` → `AbstractContainerMenu.getRedstoneSignalFromContainer()`

### Block
-   `onUse()` разделён на `useItemOn()` (с предметом) и `useWithoutItem()` (пустой рукой)
-   `appendProperties()` → `createBlockStateDefinition()`
-   `getPlacementState()` → `getStateForPlacement()`
-   `getOutlineShape()` → `getShape()`
-   `getStateForNeighborUpdate()` → `updateShape()`
-   `getComparatorOutput()` → `getAnalogOutputSignal()`
-   `isSideSolidFullSquare()` → `isFaceSturdy()`
-   `NavigationType` → `PathComputationType`
-   `VoxelShapes.cuboid()` → `Shapes.box()`

---

## 6. Capabilities и Attachments

### EntityCapability
```kotlin
// Создание
val MY_CAP: EntityCapability<MyData, Void> = EntityCapability.create(
    Mod.id("my_cap"), MyData::class.java
)

// Регистрация (требует EntityType, НЕ Class!)
event.registerEntity(MY_CAP, MyEntity::class.java) { entity, _ -> MyData(entity) }

// Для всех сущностей используйте AttachmentType вместо Capability
```

### Player Data
Если вы использовали кастомный `PlayerManager` с NBT, обновите сигнатуры:
```kotlin
fun readNbt(compound: CompoundTag, registries: HolderLookup.Provider)
fun writeNbt(compound: CompoundTag, registries: HolderLookup.Provider)
```

> 💡 **Рекомендация:** В NeoForge 1.21+ настоятельно рекомендуется использовать встроенную систему **Data Attachments** (`AttachmentType`) вместо кастомных Mixin-интерфейсов для хранения данных игроков.

---

## 7. Hex Casting API (1.21)

| Старый API                         | Новый API (1.21)                     |
| :--------------------------------- | :----------------------------------- |
| `ADIotaHolder.readIota(ServerLevel)` | `ADIotaHolder.readIota()`            |
| `ADIotaHolder.isWritable()`        | `ADIotaHolder.writeable()`           |
| `ADIotaHolder.readIotaTag()`       | `ADIotaHolder.readIota()`            |
| `MediaHelper.scanPlayerForMediaStuff` | Миксинить `MediaHelperKt` (Kotlin) |
| `CastingEnvironment.world`         | Тип `ServerLevel`                    |
| `env.impetus`                      | Проверьте актуальность в CircleCastEnv |

---

## 8. Mixins

### Kotlin Top-Level Functions
Функции верхнего уровня из файла `FileName.kt` компилируются в класс `FileNameKt`:
```java
@Mixin(targets = "at.petrak.hexcasting.api.utils.MediaHelperKt")
```

### Поиск целевых методов
**ВСЕГДА проверяйте целевые методы через декомпилятор.** Методы переименовываются между версиями.

Пример: проверка тотема в 1.21.1 находится в `hurt`, а не в `die`:
```java
@WrapOperation(
    method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
    at = @At(value = "INVOKE", 
             target = "...LivingEntity;checkTotemDeathProtection(...)Z")
)
```

### Правила
-   `remap = false` для классов из модов (Hex Casting)
-   `remap = true` (по умолчанию) для ванильных классов
-   Используйте `require = 0` только для диагностики, не оставляйте в продакшене
-   Для статических Kotlin-функций инжектор тоже должен быть `static`

---

## 9. Чеклист перед сборкой

- [ ] Все `Vec3d` → `Vec3`, `Box` → `AABB`
- [ ] Все `getNbt()` → `DataComponents.CUSTOM_DATA`
- [ ] Все `Registry.register` → `DeferredRegister` + `.value()`
- [ ] Все `initDataTracker` → `defineSynchedData` + `SynchedEntityData.defineId`
- [ ] Порядок аргументов NBT: `(CompoundTag, HolderLookup.Provider)`
- [ ] `spawnEntity()` → `addFreshEntity()`
- [ ] `isOf()` → `is()`, `increment()` → `grow()`
- [ ] `damageSources.create()` → `damageSources().source()`
- [ ] Целевые методы миксинов проверены через декомпилятор
- [ ] `BlockEntityProvider` → `EntityBlock`
- [ ] `canPlayerUse` → `stillValid`
- [ ] Capabilities регистрируются с `EntityType`, а не `Class`