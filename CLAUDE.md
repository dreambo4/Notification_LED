# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案概述

這是一個 Android 應用程式，功能是透過通知 LED 顯示自訂顏色。使用者可以透過兩種方式選擇顏色：
1. 手動輸入十六進位色碼（RGB 各兩位）
2. 使用三個 SeekBar 調整 RGB 數值（0-255）

應用程式會在 3 秒延遲後發送一個帶有自訂 LED 顏色的通知。

## 專案架構

### 單一 Activity 架構
- **Package**: `com.yr.notification_led`
- **唯一 Activity**: `MainActivity.java`
  - 包含所有 UI 邏輯和通知處理
  - 內部類別 `ColorSeekBarListener` 處理 SeekBar 的互動

### 關鍵功能實作

#### 顏色轉換 (MainActivity.java:115-135)
- `toHex(int)`: 將十進位數字轉換為兩位十六進位字串
- 自行實作轉換邏輯，未使用 Java 標準函式庫

#### 自動調整按鈕文字顏色 (MainActivity.java:83-98)
- `changeBtnColor()`: 根據背景色自動調整按鈕文字顏色
- `isColorDark(int)`: 使用色彩理論計算亮度（權重：R=0.299, G=0.587, B=0.114）
- 深色背景顯示白色文字，淺色背景顯示黑色文字

#### 通知系統 (MainActivity.java:100-163)
- 使用 `Handler.postDelayed()` 實作 3 秒延遲
- 通知 Channel ID: `"led"`
- 包含震動和聲音效果
- 使用 `setLights()` 設定 LED 顏色（亮 1000ms，滅 300ms）

#### 權限管理 (MainActivity.java:30-81)
- 需要 `POST_NOTIFICATIONS` 權限（Android 13+）
- 使用 `ActivityResultLauncher` 處理權限請求

## 建置與執行

### 前置需求
- **JDK**: 需要 Java Development Kit（推薦 OpenJDK 17 或 21）
- **Gradle**: 8.9（已設定在 gradle-wrapper.properties）
- **Android Gradle Plugin**: 8.1.2
- **編譯 SDK**: 34
- **最低 SDK**: 26
- **目標 SDK**: 34

### 建置指令

```bash
# 清理建置產物
./gradlew clean

# 建置 Debug APK
./gradlew assembleDebug

# 建置 Release APK
./gradlew assembleRelease

# 安裝到連接的裝置
./gradlew installDebug

# 執行測試
./gradlew test

# 檢查程式碼風格問題
./gradlew lint
```

### Firebase 整合
專案已整合 Firebase Crashlytics 和 Analytics：
- 設定檔：`app/google-services.json`
- Firebase BoM 版本：33.2.0
- 已啟用的服務：
  - `com.google.firebase:firebase-crashlytics`
  - `com.google.firebase:firebase-analytics`

## Gradle 設定注意事項

### 新式插件管理
專案使用 Gradle 新式配置：
- `settings.gradle` 包含 `pluginManagement` 和 `dependencyResolutionManagement`
- 根目錄 `build.gradle` 使用 `plugins` DSL
- `app/build.gradle` 使用 `plugins` DSL

### 依賴管理
主要依賴：
- AndroidX AppCompat: 1.6.1
- Material Design: 1.11.0
- ConstraintLayout: 2.1.4
- Firebase BoM: 33.2.0

## 程式碼風格

### 命名慣例
- UI 元件變數：簡短名稱（`btn1`, `btn2`, `sbr`, `sbg`, `sbb`）
- 顏色參數：使用 `colorHexString` 格式
- 內部類別：使用描述性名稱（`ColorSeekBarListener`）

### 註解語言
程式碼中的註解使用**繁體中文**，包括：
- 方法功能說明
- 重要邏輯解釋
- 變數用途說明

修改或新增程式碼時，應保持此慣例。

## Git 狀態

目前分支：`main`
最近的 commits 包含：
- Firebase Crashlytics 整合
- 自動調整按鈕背景與文字顏色功能