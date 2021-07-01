SRCFILES := $(shell find . -name "*.java")
JAVAFX_PATH := ./javafx-sdk-11.0.2/lib
MODULE_FLAG := --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml
SOURCEPATH := src/
OBJ_DIR := out/

$(OBJ_DIR): $(SRCFILES)
	javac $(MODULE_FLAG) -sourcepath $(SOURCEPATH) -d $(OBJ_DIR) $(SRCFILES)

run: $(OBJ_DIR)
	java $(MODULE_FLAG) -cp $(OBJ_DIR) Main

clean:
	rm -rf $(OBJ_DIR)