# Settings Order Patcher (Xposed Module)

[日本語](#日本語) | [English](#english)

---

## 日本語

### 概要

**LineageOS 22.x ～ 23.2専用** の設定アプリで、Googleアカウント項目の表示順序をカスタマイズするXposedモジュールです。

**RROオーバーレイではなく実行時フックを使用**しているため、クラッシュの心配なく安全に動作します。

> ℹ️ LineageOS 22.2 と LineageOS 23.2 では正常に動作確認済みです。他のLineageOS 22.x～23.x系でも動作する可能性が高いですが、保証いたしません。

### 機能

- ✅ Googleアカウント（`top_level_account_category`）を設定画面の上部に移動
- ✅ シンプルで安全な実装（AndroidxPreferenceの基底クラスをフック）
- ✅ ProGuardでリネームされた設定アプリにも対応
- ✅ 他のPreference項目のカスタマイズも容易に拡張可能

### 対応環境

- **OS**: LineageOS 22.x ～ 23.2
    - **動作確認済み**: LineageOS 22.2, LineageOS 23.2
    - **推定対応**: LineageOS 22.x ～ 23.x 系全般（動作保証なし）
- **フレームワーク**: LSPosed or Xposed派生フレームワーク
- **API Level**: 93以上のXposed APIサポート

⚠️ **注意**: 本モジュールはLineageOS専用です。純正Androidやその他のカスタムROMでの動作は未確認です。

### インストール

1. このリポジトリをクローン
   ```bash
   git clone https://github.com/butoryu1-2881/SettingsTopLevelPixelizer.git
   cd SettingsTopLevelPixelizer
   ```

2. ビルド
   ```bash
   ./gradlew assembleRelease
   ```

3. LSPosedマネージャーから APK をインストール
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

4. LSPosedで「設定」（`com.android.settings`）をスコープに追加し、有効化

5. 設定アプリを再起動

### カスタマイズ

`HookEntry.java`の以下の部分を編集することで、他のPreference項目の順序も変更できます：

```java
private static final String ACCOUNT_KEY = "top_level_account_category";
private static final int NEW_ORDER = -140;
```

複数の項目を変更する場合：

```java
if (ACCOUNT_KEY.equals(key)) {
    XposedHelpers.callMethod(thiz, "setOrder", -140);
} else if ("top_level_wifi".equals(key)) {
    XposedHelpers.callMethod(thiz, "setOrder", -100);
} else if ("top_level_display".equals(key)) {
    XposedHelpers.callMethod(thiz, "setOrder", -50);
}
```

### トラブルシューティング

**設定アプリがクラッシュする場合**

- LSPosedマネージャーで本モジュールが有効になっているか確認
- 設定アプリのキャッシュを削除：`adb shell pm clear com.android.settings`
- LSPosedのスコープに「設定」が正しくチェックされているか確認
- LineageOS 22.2 / 23.2 以外のバージョンを使用している場合、設定アプリの内部実装の差異が原因の可能性があります

**ログを確認する**

```bash
adb logcat | grep SettingsOrder
```

### 技術仕様

このモジュールは、`androidx.preference.Preference`のコンストラクタをXposedでフックすることで、Preferenceインスタンスが生成される際に`setOrder()`メソッドを呼び出します。

利点：
- Settings.apkの内部実装がProGuardでリネームされていても対応可能
- XMLの完全置き換えではなく、必要な属性値だけを変更するため安全
- `androidx.preference`ライブラリAPIは保護されているため、将来のAndroidバージョン更新にも強い

### ライセンス

MIT License - 詳細は [LICENSE](LICENSE) を参照

### 謝辞

このプロジェクトはClaudeのXposedモジュール実装ガイダンスに基づいて開発されました。

---

## English

### Overview

**LineageOS 22.x ～ 23.2 exclusive** Xposed module to customize the display order of the Google Account item in the Settings app.

**Safe and crash-free** - uses runtime hooking instead of RRO overlay.

> ℹ️ Verified working on LineageOS 22.2 and LineageOS 23.2. Likely compatible with other LineageOS 22.x～23.x versions, but not guaranteed.

### Features

- ✅ Move Google Account (`top_level_account_category`) to the top of the settings screen
- ✅ Simple and safe implementation (hooks the base Androidx Preference class)
- ✅ Compatible with ProGuard-obfuscated Settings apps
- ✅ Easy to extend for customizing other Preference items

### Requirements

- **OS**: LineageOS 22.x ～ 23.2
    - **Verified**: LineageOS 22.2, LineageOS 23.2
    - **Estimated compatible**: All LineageOS 22.x～23.x versions (not guaranteed)
- **Framework**: LSPosed or Xposed-based framework
- **API Level**: Xposed API 93 or higher

⚠️ **Note**: This module is LineageOS-exclusive. Compatibility with stock Android or other custom ROMs is not verified.

### Installation

1. Clone this repository
   ```bash
   git clone https://github.com/butoryu1-2881/SettingsTopLevelPixelizer.git
   cd SettingsTopLevelPixelizer
   ```

2. Build the APK
   ```bash
   ./gradlew assembleRelease
   ```

3. Install via LSPosed Manager
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

4. Add "Settings" (`com.android.settings`) to the module scope in LSPosed and enable it

5. Restart the Settings app

### Customization

Edit the following constants in `HookEntry.java` to change the order of other Preference items:

```java
private static final String ACCOUNT_KEY = "top_level_account_category";
private static final int NEW_ORDER = -140;
```

For multiple items:

```java
if (ACCOUNT_KEY.equals(key)) {
    XposedHelpers.callMethod(thiz, "setOrder", -140);
} else if ("top_level_wifi".equals(key)) {
    XposedHelpers.callMethod(thiz, "setOrder", -100);
} else if ("top_level_display".equals(key)) {
    XposedHelpers.callMethod(thiz, "setOrder", -50);
}
```

### Troubleshooting

**Settings app crashes**

- Verify the module is enabled in LSPosed Manager
- Clear Settings app cache: `adb shell pm clear com.android.settings`
- Confirm "Settings" is checked in the module scope
- If using a LineageOS version other than 22.2 / 23.2, differences in the Settings app internals may be the cause

**View logs**

```bash
adb logcat | grep SettingsOrder
```

### Technical Details

This module hooks the `androidx.preference.Preference` constructor to call `setOrder()` when Preference instances are created.

Benefits:
- Works even if Settings.apk's internals are obfuscated by ProGuard
- Only modifies necessary attribute values instead of replacing the entire XML
- Relies on protected `androidx.preference` library APIs, making it robust against future Android updates

### License

MIT License - See [LICENSE](LICENSE) for details

### Acknowledgments

This project was developed based on Xposed module implementation guidance from Claude (Anthropic).

---

## 開発・貢献

バグ報告や機能リクエストは [Issues](../../issues) をお願いします。

For bug reports and feature requests, please use [Issues](../../issues).