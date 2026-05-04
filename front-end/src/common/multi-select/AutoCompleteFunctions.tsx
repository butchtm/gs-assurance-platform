export const autoFocusTextInputs = (handleClose: () => void, label: string) => {
  const element = document.getElementById(`filter-element-${label}`);
  if (element) {
    const inputElements = element.getElementsByTagName("input");
    if (inputElements.length == 1) {
      const inputElement = inputElements[0];
      if (!inputElement.hasAttribute("aria-hidden")) {
        inputElement.focus();
        return;
      }
    }

    handleOpenSelectList(element, handleClose);
  }
};

const handleOpenSelectList = (filterElement: HTMLElement, handleClose: () => void) => {
  const selectListContainer = filterElement.querySelector('div[aria-haspopup="listbox"]');
  if (!selectListContainer) return;

  if (!selectListHasSelection(selectListContainer)) {
    openSelectList(selectListContainer);
  }
  setupAutoCloseEmptySelectListFlyOut(selectListContainer, handleClose);
};

const selectListHasSelection = (selectListContainer: Element): boolean => {
  const valueElement = selectListContainer.parentElement?.querySelector("input") as HTMLInputElement;
  const hasValueSelected = valueElement && !!valueElement.value;
  return hasValueSelected;
};

const openSelectList = (selectListContainer: Element) => {
  selectListContainer.dispatchEvent(new PointerEvent("mousedown", { bubbles: true }));
};

const setupAutoCloseEmptySelectListFlyOut = (selectListContainer: Element, handleClose: () => void) => {
  selectListContainer.addEventListener("focus", () => {
    setTimeout(() => {
      if (!selectListContainer) return;
      const isMenuClosed = selectListContainer.getAttribute("aria-expanded") == "false";

      if (isMenuClosed && !selectListHasSelection(selectListContainer)) {
        handleClose();
      }
    }, 200);
  });
};
