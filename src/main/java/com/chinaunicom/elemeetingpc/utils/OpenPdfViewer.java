package com.chinaunicom.elemeetingpc.utils;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.controllers.FileDetailController;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * A custom PDF viewer control extends BorderPane.
 *
 * @author chenxi 创建时间：2019-9-20 19:41:38
 */
public class OpenPdfViewer extends BorderPane implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(OpenPdfViewer.class);

    @FXML
    private Pagination pagination;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private ToolBar toolBar;

    @FXML
    private HBox zoomOptionsBox, loadOptionsBox;

    @FXML
    private Button loadButton, reduceZoomButton, addZoomButton, zoomHeightButton, zoomWidthButton;

    @FXML
    private ScrollPane scroller;

    //the file path of which is to be loaded
    String file;

    //file chooser default open path
    String initialDirectory;

    //turn on or off the loadOptionsBox and zoomOptionsBox
    Boolean zoomOptions, loadOptions;

    private Pdf pdf;

    //use platform standard file dialogs
    private FileChooser fileChooser;

    //display current page image
    SimpleObjectProperty<StackPane> currentImage;

    //zoom scaling factor
    private float zoomFactor;

    //FileDetailController
    private FileDetailController fileDetailController;

    //used to disable vertical scrolling in ScrollPane
    private EventHandler scrollEventHandler;

    //used to disable left and right arrow key on the keyboard
    private EventHandler keyEventHandler;

    private MQPlugin mQPlugin;

    /**
     * The ZoomType class define 3 types of zoom: 1, WIDTH: the zoom will fit
     * the width of the container (this is also the default zoom type) 2,
     * HEIGHT: the zoom will fit the height of the container 3, CUSTOM: zoom in
     * and zoom out by a fixed custom value as zoom factor each zoom type is
     * used to determine which method/formula should be used to calculate the
     * desirable zoom factor.
     */
    public enum ZoomType {
        WIDTH, HEIGHT, CUSTOM
    };

    private ZoomType zoomType;

    /**
     * Constructor of OpenPdfViewer
     *
     */
    public OpenPdfViewer() {
        file = "";
        pdf = null;
        zoomType = ZoomType.WIDTH;
        zoomOptions = false;
        loadOptions = false;
        //standard method to load a custom control
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/fxml_openpdfviewer.fxml"));
        //in constructor set itself as both root and controller of the PdfView.fxml 
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.getStylesheets().add(getClass().getResource("/styles/Styles.css").toString());
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            logger.error(exception.getCause().getMessage());
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //if filePath isn't empty, create a new pdf file
            if (!file.isEmpty()) {
                pdf = new Pdf(Paths.get(file));
            }
            //initialize scrollbar
            initScroller();
            //initialize mainPane's height and width property change listener
            initPropertyChangeListener();
            if (pdf != null) {
                pagination.setPageCount(pdf.numPages());
            }
            pagination.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer index) {
                    //in the sync state, speaker will send a message to the broker everytime a page is turned/a file is opened, the message contain vital infos about the file
                    //which will help followers to decide which action should be taken upon receiving them.
                    if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                        int page = pagination.getCurrentPageIndex();
                        String fileId = "\",\"bookid\":\"" + fileDetailController.getFileInfo().getFileId();
                        String fileName = ",\"fileName\":\"" + fileDetailController.getFileName();
                        String filePassword = "\",\"password\":\"" + fileDetailController.getFileInfo().getPassword();
                        String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                        String command = "\",\"command\":\"" + GlobalStaticConstant.GLOBAL_TURNPAGE;
                        String message = "{\"page\":" + page + fileName + fileId + filePassword + organName + command + "\"}";
                        mQPlugin.publishMessage(message);
                    }
                    return createPdfImage(index);
                }
            });
            HBox.setHgrow(zoomOptionsBox, Priority.ALWAYS);
            HBox.setHgrow(loadOptionsBox, Priority.ALWAYS);
            //Initialize top toolbar events
            initEventHandler();
            //Configure the top toolbar status
            updateToolbar();
            //Configure platform's default file chooser dialog
            configureFileChooser();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Add the scrollbar on the AnchorPane and bind it with the current
     * image(SimpleObjectProperty<StackPane>).
     *
     */
    private void initScroller() {
        try {
            currentImage = new SimpleObjectProperty<StackPane>();
            updateImage(0, 0, true);
            //set the new scroller on the AnchorPane
            scroller = new ScrollPane();
            AnchorPane.setTopAnchor(scroller, 0.0);
            AnchorPane.setRightAnchor(scroller, 0.0);
            AnchorPane.setLeftAnchor(scroller, 0.0);
            AnchorPane.setBottomAnchor(scroller, 70.0);
            scroller.setPannable(true);
            //one way bind the scroller to currentImage, when currentImage changes, the srcoller changes too
            //here is the crazy part, the scroller.contentProperty() is actually null because the content of the scroller is
            //never set! but somehow this binding works, amazing!
            scroller.contentProperty().bind(currentImage);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Initialize the AnchorPane mainPane's height and width property change
     * listener, when the container changes size, call the resize() method to
     * recalculate the zoom factor and update the image with the new zoom
     * factor.
     *
     */
    private void initPropertyChangeListener() {
        try {
            //add height property change listener, when the height of the AnchorPane changes, call the resize() method
            mainPane.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    resize();
                }
            });
            //add width property change listener, when the width of the AnchorPane changes, call the resize() method
            mainPane.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    resize();
                }
            });
            //add vvalue property change listener for scrollpane, provide vertical scrolling sync ability
            //note: since the mouse scrolling event has no way to tell when user will stop scrolling(unlike guesture events
            //such as setOnScrollFinish/setOnScrollStart), we simply can't get the last scrolling position, instead we send
            //out every mouse scrolling position to consumers. eg, scroll from A to E will cause this Listener send all the
            //letters from A to E inclusive. there is a known issue as if the network is unstable, there will be a huge delay
            //for the consumer to receive the message and it will become very slow for each position to load on the page :(
            //solution: make sure your network is stable and fast!
            scroller.vvalueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                        double vValue = scroller.getVvalue();
                        String fileId = ",\"bookid\":\"" + fileDetailController.getFileInfo().getFileId();
                        String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                        String command = "\",\"command\":\"" + GlobalStaticConstant.GLOBAL_VSCROLL;
                        //values below are needed for ios app in vertical scrolling sync
                        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
                        double height = resolution.getHeight();
                        double width = resolution.getWidth();
                        String type = "\",\"platformType\":\"" + GlobalStaticConstant.GLOBAL_PCSYNCFLAG;
                        String fileName = "\",\"fileName\":\"" + fileDetailController.getFileName();
                        String filePassword = "\",\"password\":\"" + fileDetailController.getFileInfo().getPassword();
                        int page = pagination.getCurrentPageIndex();
                        String message = "{\"vValue\":" + vValue + ",\"height\":" + height + ",\"width\":" + width + ",\"zoomFactor\":" + zoomFactor + ",\"page\":" + page + fileId + fileName + filePassword + type + organName + command + "\"}";
                        mQPlugin.publishMessage(message);
                    }
                }
            });
            //add hvalue property change listener for scrollpane, provide horizontal scrolling sync ability
            //note: since the mouse scrolling event has no way to tell when user will stop scrolling(unlike guesture events
            //such as setOnScrollFinish/setOnScrollStart), we simply can't get the last scrolling position, instead we send
            //out every mouse scrolling position to consumers. eg, scroll from A to E will cause this Listener send all the
            //letters from A to E inclusive. there is a known issue as if the network is unstable, there will be a huge delay
            //for the consumer to receive the message and it will become very slow for each position to load on the page :(
            //solution: make sure your network is stable and fast!
            scroller.hvalueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                        double hValue = scroller.getHvalue();
                        String fileId = ",\"bookid\":\"" + fileDetailController.getFileInfo().getFileId();
                        String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                        String command = "\",\"command\":\"" + GlobalStaticConstant.GLOBAL_HSCROLL;
                        //values below are needed for ios app in vertical scrolling sync
                        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
                        double height = resolution.getHeight();
                        double width = resolution.getWidth();
                        String type = "\",\"platformType\":\"" + GlobalStaticConstant.GLOBAL_PCSYNCFLAG;
                        String fileName = "\",\"fileName\":\"" + fileDetailController.getFileName();
                        String filePassword = "\",\"password\":\"" + fileDetailController.getFileInfo().getPassword();
                        int page = pagination.getCurrentPageIndex();
                        String message = "{\"hValue\":" + hValue + ",\"height\":" + height + ",\"width\":" + width + ",\"zoomFactor\":" + zoomFactor + ",\"page\":" + page + fileId + fileName + filePassword + type + organName + command + "\"}";
                        mQPlugin.publishMessage(message);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Define and add event handlers.
     *
     */
    private void initEventHandler() {
        try {
            //create a scrollEventHandler to prevent vertical scrolling when sync
            scrollEventHandler = new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                    if (event.getDeltaY() > 0 || event.getDeltaY() < 0) {
                        event.consume();
                    }
                }
            };
            //create a keyEventHandler to prevent clicking left or right when sync
            keyEventHandler = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                        event.consume();
                    }
                }
            };
            //tool bar button events
            EventHandler<ActionEvent> eventEventHandler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (actionEvent.getSource() == loadButton) {//define load file event
                        //open up the platform's own file choosing window by FileChooser
                        final File file = fileChooser.showOpenDialog(pagination.getScene().getWindow());
                        if (file != null) {
                            loadPdf(file.getAbsolutePath(), 0);
                        }
                    } else if (pdf != null) {
                        if (actionEvent.getSource() == addZoomButton) {//define zoom in event
                            //here set the zoomType straight to CUSTOM and zoomFactor to a multiple of 1.05
                            setZoomType(ZoomType.CUSTOM);
                            zoomFactor *= 1.05;
                            updateImage(pagination.getCurrentPageIndex(), 0, true);//update the image with new zoomFactor
                            //zoom in when sync
                            if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                                String fileId = ",\"bookid\":\"" + fileDetailController.getFileInfo().getFileId();
                                String fileName = "\",\"fileName\":\"" + fileDetailController.getFileName();
                                String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                                String command = "\",\"command\":\"" + GlobalStaticConstant.GLOBAL_ZOOMIN;
                                String message = "{\"page\":" + pagination.getCurrentPageIndex() + ",\"zoomFactor\":" + zoomFactor + fileId + fileName + organName + command + "\"}";
                                mQPlugin.publishMessage(message);
                            }
                        } else if (actionEvent.getSource() == reduceZoomButton) {//define zoom out event
                            //here set the zoomType straight to CUSTOM and zoomFactor to a multiple of .95
                            setZoomType(ZoomType.CUSTOM);
                            zoomFactor *= .95;
                            updateImage(pagination.getCurrentPageIndex(), 0, true);//update the image with new zoomFactor
                            //zoom out when sync
                            if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                                String fileId = ",\"bookid\":\"" + fileDetailController.getFileInfo().getFileId();
                                String fileName = "\",\"fileName\":\"" + fileDetailController.getFileName();
                                String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                                String command = "\",\"command\":\"" + GlobalStaticConstant.GLOBAL_ZOOMOUT;
                                String message = "{\"page\":" + pagination.getCurrentPageIndex() + ",\"zoomFactor\":" + zoomFactor + fileId + fileName + organName + command + "\"}";
                                mQPlugin.publishMessage(message);
                            }
                        } else if (actionEvent.getSource() == zoomHeightButton) {//define fit height event
                            setZoomType(ZoomType.HEIGHT);
                            updateImage(pagination.getCurrentPageIndex(), 0, true);
                            //fit height when sync
                            if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                                String fileId = ",\"bookid\":\"" + fileDetailController.getFileInfo().getFileId();
                                String fileName = "\",\"fileName\":\"" + fileDetailController.getFileName();
                                String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                                String command = "\",\"command\":\"" + GlobalStaticConstant.GLOBAL_FITHEIGHT;
                                String message = "{\"page\":" + pagination.getCurrentPageIndex() + fileId + fileName + organName + command + "\"}";
                                mQPlugin.publishMessage(message);
                            }
                        } else if (actionEvent.getSource() == zoomWidthButton) {//define fit width event
                            setZoomType(ZoomType.WIDTH);
                            updateImage(pagination.getCurrentPageIndex(), 0, true);
                            //fit width when sync
                            if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                                String fileId = ",\"bookid\":\"" + fileDetailController.getFileInfo().getFileId();
                                String fileName = "\",\"fileName\":\"" + fileDetailController.getFileName();
                                String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                                String command = "\",\"command\":\"" + GlobalStaticConstant.GLOBAL_FITWIDTH;
                                String message = "{\"page\":" + pagination.getCurrentPageIndex() + fileId + fileName + organName + command + "\"}";
                                mQPlugin.publishMessage(message);
                            }
                        }
                    }
                }
            };
            //add event handlers to the buttons
            loadButton.setOnAction(eventEventHandler);
            addZoomButton.setOnAction(eventEventHandler);
            reduceZoomButton.setOnAction(eventEventHandler);
            zoomHeightButton.setOnAction(eventEventHandler);
            zoomWidthButton.setOnAction(eventEventHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Configure the top toolbar menu to be visible or not.
     *
     */
    private void updateToolbar() {
        try {
            //if zoomOptions or loadOptions is true, set toolbar to be visible
            if (zoomOptions || loadOptions) {
                if (borderPane.getTop() == null) {
                    borderPane.setTop(toolBar);
                }
            }
            //if loadOptions is false, hide loadOptionsBox
            if (!loadOptions) {
                toolBar.getItems().remove(loadOptionsBox);
            } else {
                if (!toolBar.getItems().contains(loadOptionsBox)) {
                    toolBar.getItems().add(loadOptionsBox);
                }
            }
            //if zoomOptions is false, hide zoomOptionsBox
            if (!zoomOptions) {
                toolBar.getItems().remove(zoomOptionsBox);
            } else {
                if (!toolBar.getItems().contains(zoomOptionsBox)) {
                    toolBar.getItems().add(zoomOptionsBox);
                }
            }
            //if both zoomOptions and loadOptions are true, 
            if (zoomOptions && loadOptions) {
                if (toolBar.getItems().get(0).equals(zoomOptionsBox)) {
                    toolBar.getItems().clear();
                    toolBar.getItems().add(loadOptionsBox);
                    toolBar.getItems().add(zoomOptionsBox);
                }
            }
            //if zoomOptions and loadOptions is false, hide toolbar
            if (!zoomOptions && !loadOptions) {
                borderPane.setTop(null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Configure the platform default file chooser dialog, set default dialog
     * open path and aceptable file extension type.
     *
     */
    private void configureFileChooser() {
        try {
            initialDirectory = Paths.get(System.getProperty("user.home")).toFile().toString();
            fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(Paths.get(initialDirectory).toFile());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf", "*.PDF"));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * This method update a page image with zoomfactor/ios scale, the image is first
     * created by page index and zoom factor/ios scale it is then added on a imageView,
     * the imageView is added on a stackpane, then the stackpane is wrapped by
     * the property wrapping.
     *
     * @param index the page index
     * @param iosScale the scale factor from ios client
     * @param isNotIosRequest indicate whether the request is from ios client(should we apply ios scale or not) by default the value is true(not from ios client)
     */
    public void updateImage(int index, float iosScale, boolean isNotIosRequest) {
        try {
            //reset the vertical position for the scrollpane so everytime a page is turned(aka a new image is loaded)
            //the vertical scroller will always stay at the top right 
            if (scroller != null) {
                scroller.setVvalue(0);
            }
            //create a stackpane to put the imageView on
            StackPane stackPane = new StackPane();
            stackPane.setMinWidth(borderPane.getWidth() - 20);
            stackPane.setStyle("-fx-background-color:#ffffff;");
            //create a imageView to put the image on
            ImageView imageView = new ImageView();
            if (pdf != null) {
                if (isNotIosRequest) {
                    //recalculate the zoom factor
                    updateZoomFactor();
                    Image image = pdf.getImage(index, zoomFactor);
                    if (image != null) {
                        imageView.setImage(image);
                    }
                } else {
                    Image image = pdf.getImage(index, iosScale);
                    if (image != null) {
                        imageView.setImage(image);
                    }
                }
            }
            //add a double click mouse event to the imageView to achieve full screen effect
            addDoubleClickEffect(imageView);
            stackPane.getChildren().add(imageView);
            currentImage.set(stackPane);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }
    
    /**
     * Add a double click mouse event to the imageView to achieve full screen effect.
     * @param imageView 
     */
    public void addDoubleClickEffect(ImageView imageView) {
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    VBox mainView = fileDetailController.getMainView();
                    AnchorPane topMenu = fileDetailController.getTopMenu();
                    //in the following sync state, the zoomOptions is locked and always set to false, only allow double click full screen effect
                    if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == true) {
                        //show full screen
                        if (topMenu.isVisible() == true) {
                            mainView.getChildren().remove(topMenu);
                            topMenu.setVisible(false);
                            //get the navigation area and hide it
                            ObservableList<Node> childList = pagination.getChildrenUnmodifiable();
                            if (!childList.isEmpty()) {
                                childList.get(2).setVisible(false);
                            }
                        } else if (topMenu.isVisible() == false) { //hide full screen
                            mainView.getChildren().add(0, topMenu);
                            topMenu.setVisible(true);
                            //get the navigation area and show it
                            ObservableList<Node> childList = pagination.getChildrenUnmodifiable();
                            if (!childList.isEmpty()) {
                                childList.get(2).setVisible(true);
                            }
                        }
                    } else if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == false) {
                        //show full screen
                        if (zoomOptions == true && loadOptions == false) {
                            zoomOptions = false;
                            loadOptions = false;
                            updateToolbar();
                            mainView.getChildren().remove(topMenu);
                            //get the navigation area and hide it
                            ObservableList<Node> childList = pagination.getChildrenUnmodifiable();
                            if (!childList.isEmpty()) {
                                childList.get(2).setVisible(false);
                            }
                        } else if (zoomOptions == false && loadOptions == false) { //hide full screen
                            zoomOptions = true;
                            loadOptions = false;
                            updateToolbar();
                            mainView.getChildren().add(0, topMenu);
                            //get the navigation area and show it
                            ObservableList<Node> childList = pagination.getChildrenUnmodifiable();
                            if (!childList.isEmpty()) {
                                childList.get(2).setVisible(true);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Calculate the desirable zooming scaling factor for the page image when
     * updateImage, resize, loadPDf and setZoomType, it's done by either using
     * the container's width/height divided into the image's width/height, the
     * formula equals the container's(width/height) /
     * page(image)'s(width/height).
     *
     */
    private void updateZoomFactor() {
        try {
            if (zoomType == ZoomType.WIDTH) {
                if (pdf != null && pdf.getImage(pagination.getCurrentPageIndex()) != null) {
                    double mainPaneWidth = mainPane.getWidth();//the container's width
                    double imageWidth = pdf.getImage(pagination.getCurrentPageIndex()).getWidth();
                    //note: here I add some codes to handle 16:9 ratio ppt, user wants to view the ppt content on a single page
                    //rather than scrolling it down/up bit by bit(fit width) therefore I mark the height as the zooming
                    //factor(mainPaneHeight/imageHeight) to make sure it will always fit the height of the view, but this
                    //will leave two blanket vertical gap on both side of the view, the reason is that the app is not design
                    //and build by the way that it can adjust 16:9 ppt ratio, plus the height of app will also changes when double
                    //click to max/mini the file viewing screen, this problem is difficult to solve at the moment and require a further
                    //improvement ;)
                    //note: the 16:9 ratio is calculated by the width/height, when the result(zoomfacto) is close to over 1.77, we can be
                    //sure it's 16:9 ratio, as a fact most ppt use this ratio as default
                    double mainPaneHeight = mainPane.getHeight();
                    double imageHeight = pdf.getImage(pagination.getCurrentPageIndex()).getHeight();
                    if (imageWidth > imageHeight) {
                        zoomFactor = (float) (mainPaneHeight / imageHeight);//this is the best solution at the moment
                    } else {
                        zoomFactor = (float) ((mainPaneWidth - 20) / imageWidth);//other doc other than ppt
                    }
                }
            } else if (zoomType == ZoomType.HEIGHT) {
                if (pdf != null && pdf.getImage(pagination.getCurrentPageIndex()) != null) {
                    double mainPaneHeight = mainPane.getHeight();//the desirable height
                    double imageHeight = pdf.getImage(pagination.getCurrentPageIndex()).getHeight();//the image's height
                    zoomFactor = (float) ((mainPaneHeight - 70) / imageHeight);//the desirable zooming scaling
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * This method is called when the size(aka width & height) of the container
     * changes, it calls the updateZoomFactor() method to calculate the new zoom
     * factor first, then it load the page image with the new zoom factor and
     * page index by calling updateImage(int index) method.
     *
     */
    private void resize() {
        updateZoomFactor();
        updateImage(pagination.getCurrentPageIndex(), 0, true);
    }

    /**
     * This is the callback method which is used by the
     * pagination.setPageFactory, The callback method is called when a page has
     * been selected, It loads and returns the content of the selected page,
     * note: simply going through pages doesn't make the zoom to change
     * therefore do not require the zoom factor to be set.
     *
     * @param index current page index
     *
     */
    private Node createPdfImage(int index) {
        updateImage(index, 0, true);
        return scroller;
    }

    /**
     * Open up a render ready PDF file with zoomfactor and pagination set by the
     * given file path and pageIndex.
     *
     * @param path the string value of the path of the file to be opened
     * @param pageIndex default pageIndex
     */
    public void loadPdf(String path, int pageIndex) {
        try {
            //close the previously opened document first, which will never happen in our app since
            //each time user changes files in the app, the program destroy the old view
            //and start up a brand new view, hence the OpenPdfViewer itself also get loaded
            //and initialize again (pdf = "")
            if (pdf != null) {
                pdf.getDocument().close();
            }
            //open up a new pdf document
            pdf = new Pdf(Paths.get(path));
            //calculate its' zoom factor
            updateZoomFactor();
            //set pagination
            pagination.setPageCount(pdf.numPages());
            pagination.setCurrentPageIndex(pageIndex);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * Open up a render ready encrypted PDF file with zoomfactor and pagination
     * set by the given file path， password and pageIndex.
     *
     * @param path the string value of the path of the file to be opened
     * @param password the password of the file
     * @param pageIndex default pageIndex
     */
    public void loadPdf(String path, String password, int pageIndex) {
        try {
            //close the previously opened document first, which will never happen in our app since
            //each time user changes files in the app, the program destroy the old view
            //and start up a brand new view, hence the OpenPdfViewer itself also get loaded
            //and initialize again (pdf = "")
            if (pdf != null) {
                pdf.getDocument().close();
            }
            //open up a new pdf document
            pdf = new Pdf(Paths.get(path), password);
            //calculate its' zoom factor
            updateZoomFactor();
            //set pagination
            pagination.setPageCount(pdf.numPages());
            pagination.setCurrentPageIndex(pageIndex);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * Close a PDF file.
     *
     */
    public void closePdf() {
        try {
            if (pdf != null) {
                pdf.getDocument().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * The getZoomType method must be used in combine with setZoomType method in
     * order to make setZoomType work properly
     *
     * @return zoomType
     */
    public ZoomType getZoomType() {
        return zoomType;
    }

    /**
     * Set the zoom type value, called by the FXMLLoader when first load to pass
     * the value defined by the element
     * inside<OpenPdfViewer ...zoomType="WIDTH"></OpenPdfViewer>, everytime the
     * new zoom type is set, the zoom factor is recalculated.
     *
     * @param zoomType
     */
    public void setZoomType(ZoomType zoomType) {
        this.zoomType = zoomType;
        updateZoomFactor();
    }

    /**
     * Set the zoomFactor value.
     *
     * @param zoomFactor
     */
    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    /**
     * The getZoomOptions method must be used in combine with setZoomOptions
     * method in order to make setZoomOptions work properly
     *
     * @return zoomOptions
     */
    public Boolean getZoomOptions() {
        return zoomOptions;
    }

    /**
     * Set the zoom options value, called by the FXMLLoader when first load to
     * pass the value defined by the element
     * inside<OpenPdfViewer ...zoomOptions="true"></OpenPdfViewer>, everytime
     * the new zoom options is set, the toolbar is updated.
     *
     * @param zoomOptions
     */
    public void setZoomOptions(Boolean zoomOptions) {
        this.zoomOptions = zoomOptions;
        updateToolbar();
    }

    /**
     * The getLoadOptions method must be used in combine with setLoadOptions
     * method in order to make setLoadOptions work properly
     *
     * @return loadOptions
     */
    public Boolean getLoadOptions() {
        return loadOptions;
    }

    /**
     * Set the load option value, called by the FXMLLoader when first load to
     * pass the value defined by the element
     * inside<OpenPdfViewer ...loadOptions="true"></OpenPdfViewer>, everytime
     * the new load option is set, the toolbar is updated.
     *
     * @param loadOptions
     */
    public void setLoadOptions(Boolean loadOptions) {
        this.loadOptions = loadOptions;
        updateToolbar();
    }

    public String getInitialDirectory() {
        return initialDirectory;
    }

    public void setInitialDirectory(String initialDirectory) {
        this.initialDirectory = initialDirectory;
        fileChooser.setInitialDirectory(Paths.get(initialDirectory).toFile());
    }

    public void setFile(String file) {
        this.file = file;
        loadPdf(file, 0);
    }

    /**
     * Set the vertical value for the scrollPane.
     *
     * @param value
     */
    public void setVvalue(double value) {
        scroller.setVvalue(value);
    }

    /**
     * Set the horizontal value for the scrollPane.
     *
     * @param value
     */
    public void setHvalue(double value) {
        scroller.setHvalue(value);
    }

    public String getFile() {
        return file;
    }

    /**
     * Set FileDetailController.
     *
     * @param fileDetailController
     */
    public void setFileDetaiController(FileDetailController fileDetailController) {
        this.fileDetailController = fileDetailController;
    }

    /**
     * Go to the current page index, for external class which need to access
     * pagination.setCurrentPageIndex method only.
     *
     * @param pageIndex
     */
    public void goToCurrentPage(int pageIndex) {
        try {
            pagination.setCurrentPageIndex(pageIndex);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * Get the current page index, for external class which need to access
     * pagination.getCurrentPageIndex method only.
     *
     * @param pageIndex
     */
    public int getCurrentPage() {
        return pagination.getCurrentPageIndex();
    }

    /**
     * Set whether to lock/unlock the pagination navigation area(aka
     * PaginationSkin) by the given boolean input, also lock/unlock the left and
     * right key.
     *
     * @param isLock
     */
    public void isLockPagination(boolean isLock) {
        try {
            ObservableList<Node> childList = pagination.getChildrenUnmodifiable();
            if (!childList.isEmpty()) {
                childList.get(2).setDisable(isLock);
            }
            //lock/unlock left and right arrow key on the keyboard
            if (isLock == true) {
                pagination.addEventFilter(KeyEvent.ANY, keyEventHandler);
            } else {
                pagination.removeEventFilter(KeyEvent.ANY, keyEventHandler);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Set whether to lock/unlock the ScrollPane and the vertical scroller by
     * the given boolean input.
     *
     * @param isLock
     */
    public void isLockScroller(boolean isLock) {
        try {
            //lock/unlock the vertical scroll button
            ObservableList<Node> childList = scroller.getChildrenUnmodifiable();
            if (!childList.isEmpty()) {
                childList.get(1).setDisable(isLock);
                childList.get(2).setDisable(isLock);
            }
            //lock/unlock vertical scrolling & pannable
            if (isLock == true) {
                scroller.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
                scroller.setPannable(false);
            } else {
                scroller.removeEventFilter(ScrollEvent.ANY, scrollEventHandler);
                scroller.setPannable(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    public void setMQPlugin(MQPlugin mQPlugin) {
        try {
            if (mQPlugin != null) {
                this.mQPlugin = mQPlugin;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }
}

/**
 * This inner class reprent a render ready PDF file, it takes file path as param
 * and load a render ready pdf file.
 *
 * @author chenxi 创建时间：2019-9-21 9:41:38
 */
class Pdf {

    private static final Logger logger = LoggerFactory.getLogger(Pdf.class);

    //represent a PDF file
    private PDDocument document;
    //used to renders a PDF file to an AWT BufferedImage
    private PDFRenderer renderer;

    //construct a render ready PDF file by the given path
    public Pdf(Path path) {
        try {
            document = PDDocument.load(path.toFile());//load a PDF file file
            renderer = new PDFRenderer(document);//pass the file for BufferedImage rendering
            //document.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    //construct a render ready PDF file by the given path and password
    public Pdf(Path path, String password) {
        try {
            document = PDDocument.load(path.toFile(), password);//load a PDF file with password
            renderer = new PDFRenderer(document);//pass the file for BufferedImage rendering
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Get the total page number counts in the document.
     *
     * @return total page number
     */
    public int numPages() {
        return document.getPages().getCount();
    }

    /**
     * Rendering a page into a JavaFX image, returns the given page as an RGB
     * image at default 72 DPI.
     *
     * @param pageNumber the page to be rendered
     * @return the image of a page
     */
    public Image getImage(int pageNumber) {
        BufferedImage pageImage = null;
        try {
            if (pageNumber <= document.getNumberOfPages()) {
                pageImage = renderer.renderImage(pageNumber);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
            return null;
        }
        //the rendered page is a BufferedImage type, need to convert it to Image
        return SwingFXUtils.toFXImage(pageImage, null);
    }

    /**
     * Returns the given page as an RGB image at the given scale, A scale of 1
     * will render at 72 DPI.
     *
     * @param pageNumber the page to be rendered
     * @param scaleFactor the zoom scaling factor
     * @return the image of a page
     */
    public Image getImage(int pageNumber, float scaleFactor) {
        BufferedImage pageImage;
        try {
            if (pageNumber <= document.getNumberOfPages()) {
                if (scaleFactor >= 0.1) {
                    pageImage = renderer.renderImage(pageNumber, scaleFactor);
                } else {
                    pageImage = renderer.renderImage(pageNumber);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
            return null;
        }
        //the rendered page is a BufferedImage type, need to convert it to Image
        return SwingFXUtils.toFXImage(pageImage, null);
    }

    public PDDocument getDocument() {
        return document;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }
}
