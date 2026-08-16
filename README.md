# PoopSky: Flyimpact

空中厕所：**蝇趴**（PoopSky: Flyimpact）是 [PoopSky](https://github.com/Altnoir/PoopSkyMod) 的 NeoForge 1.21.1 附属模组。

## 内容

- **皮革苍蝇**（`leather`）：苍蝇桶产出皮革。繁育箱中棕苍蝇 + 白苍蝇，20% 变异。
- **腐肉苍蝇**（`rotten_flesh`）：苍蝇桶产出腐肉。繁育箱中棕苍蝇 + 橙苍蝇，20% 变异。
- **烈焰苍蝇**（`blaze`）：对苍蝇实体喂食烈焰棒获得；也可由火龙果苍蝇 + 橙苍蝇 20% 变异。苍蝇桶产出烈焰粉。
- **JEI 繁育箱转移**：打开繁育箱并查看配方时，若背包已有对应品种苍蝇，可一键填入两个苍蝇槽。

## 依赖

- Minecraft 1.21.1
- NeoForge 21.1.x
- PoopSky 2.0 及以上（需要苍蝇桶 / 繁育箱配方系统）
- JEI（可选，用于配方转移）

开发时可将 PoopSky 的 jar 放到 `libs/` 目录，Gradle 会优先使用本地文件。

## 构建

```bash
./gradlew build
```

产物位于 `build/libs/`。
