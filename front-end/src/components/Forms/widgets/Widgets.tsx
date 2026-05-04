import { RegistryWidgetsType, Widget } from "@rjsf/utils";

import { CustomMultiSelectWidget } from "./CustomMultiSelectWidget";
import { CustomLargeTextWidget } from "./CustomLargeTextWidget";
import CustomDeclarationCheckboxWidget from "./CustomDeclarationCheckboxWidget";

const widgets: RegistryWidgetsType = {
  ChipMultiSelectWidget: CustomMultiSelectWidget, // Custom widget name for multi-select with chips
  LargeTextWidget: CustomLargeTextWidget,
  DeclarationCheckboxWidget: CustomDeclarationCheckboxWidget as Widget,
};

export { widgets };
