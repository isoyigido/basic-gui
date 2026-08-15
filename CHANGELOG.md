## [0.8.1](https://github.com/isoyigido/basic-gui/compare/0.8.0...0.8.1) (2026-08-15)

### Bug Fixes

* **core:** capture overlay reference locally to prevent null race condition during rendering and updating ([bc44cd5](https://github.com/isoyigido/basic-gui/commit/bc44cd502bfbf5d83caff726b6578b1f9f594209))

## [0.8.0](https://github.com/isoyigido/basic-gui/compare/0.7.0...0.8.0) (2026-08-15)

### Features

* **loader:** enable attribute and constant resolution in GUILoader constant declarations ([f88cf08](https://github.com/isoyigido/basic-gui/commit/f88cf08ab744c79da38f51eeabf1b929bc13f459))

### Bug Fixes

* **core:** skip updates and input events for non-visible widgets ([4673401](https://github.com/isoyigido/basic-gui/commit/4673401ee5481240073e9974384d6d477b9420bd))

## [0.7.0](https://github.com/isoyigido/basic-gui/compare/0.6.0...0.7.0) (2026-08-01)

### Features

* **app:** introduce Theme class for color theme and font management and remove legacy App class ([03279d8](https://github.com/isoyigido/basic-gui/commit/03279d8f65eb15ac18fd36b7fce24b1cc657889b))
* **loader:** add bold and italic optional parameters to TextComponentBuilder ([96723c4](https://github.com/isoyigido/basic-gui/commit/96723c4feb21b8df71d7c4b79c7682deb6b1bd81))
* **loader:** introduce TextParameter for literal and translation-key text parsing ([b1cd265](https://github.com/isoyigido/basic-gui/commit/b1cd26526a800305a02dfa5dd0e130827382ebea))
* **loader:** update ColorParameter to support Theme color keys and streamline parsing rules ([63906b2](https://github.com/isoyigido/basic-gui/commit/63906b268f0a9fdd80f3415ad6bb7855ea46c6a2))

### Bug Fixes

* **loader:** defer GUI building in GUILoader via content caching for dynamic language and appearance updates ([fdd0afd](https://github.com/isoyigido/basic-gui/commit/fdd0afd574272403b614937678c289adea61ad3b))

### Performance Improvements

* optimize map lookups by replacing containsKey checks with null checks ([0ca90d4](https://github.com/isoyigido/basic-gui/commit/0ca90d40a153786e502e2466d4fa84bce219440f))

## [0.6.0](https://github.com/isoyigido/basic-gui/compare/0.5.0...0.6.0) (2026-07-29)

### Features

* **core:** add GUIRegistry and name-based GUI switching support in GUIManager ([ec67207](https://github.com/isoyigido/basic-gui/commit/ec672078d9014a885dc539dc29718232f5415779))

## [0.5.0](https://github.com/isoyigido/basic-gui/compare/0.4.0...0.5.0) (2026-07-28)

### Features

* **loader:** add constant declaration support to GUILoader ([f267bf4](https://github.com/isoyigido/basic-gui/commit/f267bf46510abbb80532cad9ec426abcb12f3923))
* **loader:** add optional visibility parameter to WidgetBuilder ([f4c0a15](https://github.com/isoyigido/basic-gui/commit/f4c0a15c771f4e6ba439ffbef8f22e709da837a1))
* **loader:** add support for multi-line list parameters in GUI files ([024de10](https://github.com/isoyigido/basic-gui/commit/024de10c087948cf22acac4a9dc5acd3b758648e))
* **loader:** add widget attribute access support to GUILoader ([7c21ae0](https://github.com/isoyigido/basic-gui/commit/7c21ae088fc346cade12aca987eac0c724a3252a))
* **loader:** implement BooleanParameter for parsing boolean attributes ([f6ef80f](https://github.com/isoyigido/basic-gui/commit/f6ef80f123f641e8462152adc7f9254296694cd1))
* **loader:** implement WidgetParameter for referencing named widgets ([1b8fc08](https://github.com/isoyigido/basic-gui/commit/1b8fc088bc1098a6ededdfcdbf6c69f20d294a6f))

### Performance Improvements

* **loader:** add a cache in GUILoader to avoid parsing the same GUI file twice ([17e1a71](https://github.com/isoyigido/basic-gui/commit/17e1a717ae4a12e5ad4d235b3435bd83f9422eef))

## [0.4.0](https://github.com/isoyigido/basic-gui/compare/0.3.0...0.4.0) (2026-07-23)

### Features

* **core:** implement GUI file parsing and widget builder registry system ([a20f056](https://github.com/isoyigido/basic-gui/commit/a20f056295cb271b47e6cda7a1f4bc424e461484))
* **core:** implement ImageComponentBuilder and TextComponentBuilder for GUI loading ([f4368ed](https://github.com/isoyigido/basic-gui/commit/f4368ed01fcde841589cfac7ea56a4ee05c8149d))
* **loader:** implement ColorParameter for parsing color attributes ([30c7d4f](https://github.com/isoyigido/basic-gui/commit/30c7d4f7dac2503e40274d82150efcfa62e0a2af))
* **loader:** implement FloatParameter for parsing float attributes ([4ec6918](https://github.com/isoyigido/basic-gui/commit/4ec69180b01e4240fa5260439a45ca08348e6edc))
* **loader:** implement ImageResourceParameter for loading image assets from resources ([a25d3e5](https://github.com/isoyigido/basic-gui/commit/a25d3e5bcee19ebfb4a86bb1392c364603eeb327))
* **loader:** implement StringParameter for parsing string attributes ([93c13c6](https://github.com/isoyigido/basic-gui/commit/93c13c6a61004a755caec05f97c961549dfcc6fd))

### Bug Fixes

* change Optional.of call to Optional.ofNullable where the argument could be null ([d8e1b05](https://github.com/isoyigido/basic-gui/commit/d8e1b05855a9174b5777573739b03f9fe6595da1))

## [0.3.0](https://github.com/isoyigido/basic-gui/compare/0.2.0...0.3.0) (2026-07-06)

### Features

* **core:** implement ImageComponent for image rendering ([27d640e](https://github.com/isoyigido/basic-gui/commit/27d640ec8f45b1cf5f7db2cad2851b74704680c2))

### Bug Fixes

* **core:** change some protected methods to public ([5e3b60e](https://github.com/isoyigido/basic-gui/commit/5e3b60e52fae2a05bd560e9578ecc5f50f1d4750))

## [0.2.0](https://github.com/isoyigido/basic-gui/compare/0.1.0...0.2.0) (2026-07-04)

### Features

* **core:** add a placeholder constructor in Component ([c16249c](https://github.com/isoyigido/basic-gui/commit/c16249cafe03d48ed9711b16b56490e7236a65d6))
* **core:** implement TextComponent for text rendering ([7016352](https://github.com/isoyigido/basic-gui/commit/7016352395ca7b45148e8d9d4ee9123bf28c86b5))
* **core:** implement Trigger component for interactive areas ([e1e75fd](https://github.com/isoyigido/basic-gui/commit/e1e75fd8ac7f8f69a54991412f2a5272943256f7))

## [0.1.0](https://github.com/isoyigido/basic-gui/compare/0.0.1...0.1.0) (2026-07-04)

### Features

* **core:** add method for removing global overlay ([7cd3ee2](https://github.com/isoyigido/basic-gui/commit/7cd3ee242a1f6e71353a13d7d9d46fb6c853e0b7))

## [0.0.1](https://github.com/isoyigido/basic-gui/compare/0.0.0...0.0.1) (2026-07-04)

### Performance Improvements

* add a check to prevent setting a cursor if it is already set ([17062c5](https://github.com/isoyigido/basic-gui/commit/17062c5f382339c1b505eddc23779ee72a691fff))
