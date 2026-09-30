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
- 予定の繰り返し（毎週／毎月）に対応。1回分だけの例外編集・除外は未対応（フェーズ2）。
- 家計簿サマリーのグラフはシンプルな棒表示（Compose標準コンポーネントのみ）。要件定義書にあるチャートライブラリ（Vico等）の導入は未着手。
- クラウド同期（Firebase）、通知・リマインダーなどフェーズ2以降の機能は未実装。

## ビルド確認状況

`./gradlew assembleDebug` および `./gradlew lintDebug` がエラー0件で成功することを確認済み（警告47件、いずれもcontentDescription未設定などの軽微なもの）。ユニットテストは未実装（`testDebugUnitTest` はNO-SOURCE）。
実機・エミュレータでの起動確認は未実施のため、Android Studioで一度動作確認することを推奨します。
