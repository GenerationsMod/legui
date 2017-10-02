## [Unreleased]

## [1.2.0]
### Changes
- Removed event processor from context
- Event processors moved to another package
- Some changes in nvg renderer structure
- Removed renderer provider from constructor.
  
## [1.1.10]
### Fixed
- Fixed issue with widget minimize button.

## [1.1.9]
### Fixed
- Fixed some mistakes with generics diamonds.

## [1.1.8] - 2017-08-29
### Fixed
- Fixed issue with widget title height.
- Fixed issue with backspace and delete action on cleared TextInput and TextArea with `setText("")` method.
- Fixed TextArea key event listener (can't select all text in non-editable TextArea).

## [1.1.5] - 2017-08-25
### Fixed
- Removed debug option from NvgRenderer.
- Removed states from nvg renderers.
- Fixed nvg text renderers.
- Fixed issue with ImageIcon (there was used LoadableImage when should be Image).
- Added some javadocs.