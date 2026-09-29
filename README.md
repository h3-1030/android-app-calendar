# パーソナル統合カレンダー

予定・todo・日記・家計簿を1つのカレンダーで管理する個人利用向けAndroidアプリ。
仕様の詳細は [docs/requirements.md](docs/requirements.md) を参照。

## 技術スタック

- Kotlin + Jetpack Compose（Material3）
- Room（ローカルDB）
- Navigation Compose
- MVVM（ViewModel + StateFlow）

## ビルド方法

Android Studio（Ladybug以降推奨）でプロジェクトルートを開き、Gradle同期後に通常どおり実行してください。
コマンドラインの場合:

```
./gradlew assembleDebug
```

- minSdk 26 / target・compileSdk 35
- 初回起動時に家計簿の初期カテゴリ（支出：外食・アミューズメント・カフェ・コーヒー豆・食材／収入：給与）が自動で登録されます。

## 現状（雛形段階）

- データ層（Room：Entity / DAO / Database）、Repository層、5画面（カレンダー・日別詳細・Todo一覧・家計簿サマリー・設定）とクイック追加を実装済み。
- 家計簿サマリーのグラフはシンプルな棒表示（Compose標準コンポーネントのみ）。要件定義書にあるチャートライブラリ（Vico等）の導入は未着手。
- クラウド同期（Firebase）、通知・リマインダー、予定の繰り返し例外編集などフェーズ2以降の機能は未実装。

## 注記

このリポジトリのクラウド開発環境ではネットワークポリシー上 `dl.google.com`（Android Gradle PluginやAndroidX等が配布されるGoogle Mavenリポジトリ）への接続がブロックされているため、この環境内では実際のGradleビルド・依存関係解決を検証できていません。Android StudioやCI等、Google Mavenに到達できる環境で最初のビルドを行ってください。
