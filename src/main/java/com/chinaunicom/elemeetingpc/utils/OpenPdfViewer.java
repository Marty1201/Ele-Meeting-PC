package com.chinaunicom.elemeetingpc.utils;

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
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * A custom PDF viewer control extends BorderPane.
 *
 * @author chenxi 创建时间：2019-9-20 19:41:38
 */
public class OpenPdfViewer extends BorderPane implements Initializable {

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
     * Constructor of OpenPdfViewer, todo: add filePath as the param which set
     * the opened file path
     *
     */
    public OpenPdfViewer() {
        file = ""; //todo: set file path
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
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        pagination.setPageFactory(index -> createPdfImage(index));

        HBox.setHgrow(zoomOptionsBox, Priority.ALWAYS);
        HBox.setHgrow(loadOptionsBox, Priority.ALWAYS);
        //Initialize top toolbar events
        initEventHandler();
        //Configure the top toolbar status
        updateToolbar();
        //Configure platform's default file chooser dialog
        configureFileChooser();
    }

    /**
     * Add the scrollbar on the AnchorPane and bind it with the current
     * image(SimpleObjectProperty<StackPane>).
     *
     */
    private void initScroller() {
        currentImage = new SimpleObjectProperty<StackPane>();
        updateImage(0);
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
    }

    /**
     * Initialize the AnchorPane mainPane's height and width property change
     * listener, when the container changes size, call the resize() method to
     * recalculate the zoom factor and update the image with the new zoom
     * factor.
     *
     */
    private void initPropertyChangeListener() {
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
    }

    /**
     * Define and add event handlers for the top toolbar.
     *
     */
    private void initEventHandler() {
        EventHandler<ActionEvent> eventEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent.getSource() == loadButton) {//define load file event
                    //open up the platform's own file choosing window by FileChooser
                    final File file = fileChooser.showOpenDialog(pagination.getScene().getWindow());
                    if (file != null) {
                        loadPdf(file.getAbsolutePath());
                    }
                } else if (pdf != null) {
                    if (actionEvent.getSource() == addZoomButton) {//define zoom in event
                        //here set the zoomType straight to CUSTOM and zoomFactor to a multiple of 1.05
                        setZoomType(ZoomType.CUSTOM);
                        zoomFactor *= 1.05;
                        updateImage(pagination.getCurrentPageIndex());//update the image with new zoomFactor

                    } else if (actionEvent.getSource() == reduceZoomButton) {//define zoom out event
                        //here set the zoomType straight to CUSTOM and zoomFactor to a multiple of .95
                        setZoomType(ZoomType.CUSTOM);
                        zoomFactor *= .95;
                        updateImage(pagination.getCurrentPageIndex());//update the image with new zoomFactor

                    } else if (actionEvent.getSource() == zoomHeightButton) {//define fit height event
                        setZoomType(ZoomType.HEIGHT);
                        updateImage(pagination.getCurrentPageIndex());

                    } else if (actionEvent.getSource() == zoomWidthButton) {//define fit width event
                        setZoomType(ZoomType.WIDTH);
                        updateImage(pagination.getCurrentPageIndex());
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
    }

    /**
     * Configure the top toolbar menu to be visible or not.
     *
     */
    private void updateToolbar() {
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
    }

    /**
     * Configure the platform default file chooser dialog, set default dialog
     * open path and aceptable file extension type.
     *
     */
    private void configureFileChooser() {
        initialDirectory = Paths.get(System.getProperty("user.home")).toFile().toString();
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Paths.get(initialDirectory).toFile());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf", "*.PDF"));
    }

    /**
     * This method update a page image with zoomfactor, the image is first
     * created by page index and zoom factor it is then added on a imageView,
     * the imageView is added on a stackpane, then the stackpane is wrapped by
     * the property wrapping.
     *
     * @param index the page index
     */
    private void updateImage(int index) {
        //recalculate the zoom factor
        updateZoomFactor();
        //reset the vertical position for the scrollpane so everytime a page is turned(aka a new image is loaded)
        //the vertical scroller will always stay at the top right 
        if (scroller != null) {
            scroller.setVvalue(0);
        }
        //create a stackpane to put the imageView on
        StackPane stackPane = new StackPane();
        stackPane.setMinWidth(borderPane.getWidth() - 20);
        //create a imageView to put the image on
        ImageView imageView = new ImageView();
        if (pdf != null) {
            Image image = pdf.getImage(index, zoomFactor);
            if (image != null) {
                imageView.setImage(image);
            }
        }
        stackPane.getChildren().add(imageView);
        currentImage.set(stackPane);
    }

    /**
     * Calculate the desirable zooming scaling factor by the container's
     * width/height, it is calculated by the container's width/height (the
     * desirable value which can change by resizing) divided the width/height of
     * the page image(with a fixed value), the goal is to attain a desirable
     * scaling to adapt the container's size change), the formula equals the
     * container's(width/height) / page(image)'s(width/height).
     *
     */
    private void updateZoomFactor() {
        if (zoomType == ZoomType.WIDTH) {
            if (pdf != null && pdf.getImage(pagination.getCurrentPageIndex()) != null) {
                double mainPaneWidth = mainPane.getWidth();//the desirable width
                double imageWidth = pdf.getImage(pagination.getCurrentPageIndex()).getWidth();//the image's width
                zoomFactor = (float) ((mainPaneWidth - 20) / imageWidth);//the desirable zooming scaling
            }
        } else if (zoomType == ZoomType.HEIGHT) {
            if (pdf != null && pdf.getImage(pagination.getCurrentPageIndex()) != null) {
                double mainPaneHeight = mainPane.getHeight();//the desirable height
                double imageHeight = pdf.getImage(pagination.getCurrentPageIndex()).getHeight();//the image's height
                zoomFactor = (float) ((mainPaneHeight - 70) / imageHeight);//the desirable zooming scaling
            }
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
        updateImage(pagination.getCurrentPageIndex());
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
        updateImage(index);
        return scroller;
    }

    /**
     * Open up a render ready PDF file with zoomfactor and pagination set by the
     * given file path.
     *
     * @param path the string value of the path of the file to be opened
     */
    public void loadPdf(String path) {
        try {
            //close the previously opened document first
            if (pdf != null) {
                pdf.getDocument().close();
            }
            //open up a new pdf document
            pdf = new Pdf(Paths.get(path));
            //calculate its' zoom factor
            updateZoomFactor();
            //set pagination
            pagination.setPageCount(pdf.numPages());
            pagination.setCurrentPageIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Open up a render ready encrypted PDF file with zoomfactor and pagination set by the
     * given file path and password.
     *
     * @param path the string value of the path of the file to be opened
     * @param password the password of the file
     */
    public void loadPdf(String path, String password) {
        try {
            //close the previously opened document first
            if (pdf != null) {
                pdf.getDocument().close();
            }
            //open up a new pdf document
            pdf = new Pdf(Paths.get(path), password);
            //calculate its' zoom factor
            updateZoomFactor();
            //set pagination
            pagination.setPageCount(pdf.numPages());
            pagination.setCurrentPageIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Close a PDF file.
     *
     */
    public void closePdf() {
        try {
            pdf.getDocument().close();
        } catch (Exception e) {
            e.printStackTrace();
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
     * The getZoomOptions method must be used in combine with setZoomOptions method in
     * order to make setZoomOptions work properly
     *
     * @return zoomOptions
     */
    public Boolean getZoomOptions() {
        return zoomOptions;
    }

    /**
     * Set the zoom options value, called by the FXMLLoader when first load to pass
     * the value defined by the element
     * inside<OpenPdfViewer ...zoomOptions="true"></OpenPdfViewer>, everytime the
     * new zoom options is set, the toolbar is updated.
     *
     * @param zoomOptions
     */
    public void setZoomOptions(Boolean zoomOptions) {
        this.zoomOptions = zoomOptions;
        updateToolbar();
    }

    /**
     * The getLoadOptions method must be used in combine with setLoadOptions method in
     * order to make setLoadOptions work properly
     *
     * @return loadOptions
     */
    public Boolean getLoadOptions() {
        return loadOptions;
    }

    /**
     * Set the load option value, called by the FXMLLoader when first load to pass
     * the value defined by the element
     * inside<OpenPdfViewer ...loadOptions="true"></OpenPdfViewer>, everytime the
     * new load option is set, the toolbar is updated.
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
        loadPdf(file);
    }

    public String getFile() {
        return file;
    }
}

/**
 * This inner class reprent a render ready PDF file, it takes file path as param
 * and load a render ready pdf file.
 *
 * @author chenxi 创建时间：2019-9-21 9:41:38
 */
class Pdf {

    //represent a PDF file
    private PDDocument document;
    //used to renders a PDF file to an AWT BufferedImage
    private PDFRenderer renderer;

    //construct a render ready PDF file by the given path
    public Pdf(Path path) {
        try {
            document = PDDocument.load(path.toFile());//load a PDF file file
            renderer = new PDFRenderer(document);//pass the file for BufferedImage rendering
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //construct a render ready PDF file by the given path and password
    public Pdf(Path path, String password) {
        try {
            document = PDDocument.load(path.toFile(), password);//load a PDF file with password
            renderer = new PDFRenderer(document);//pass the file for BufferedImage rendering
        } catch (IOException ex) {
            ex.printStackTrace();
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
            return null;
        }
        //the rendered page is a BufferedImage type, need to convert it to Image
        return SwingFXUtils.toFXImage(pageImage, null);
    }

    public PDDocument getDocument() {
        return document;
    }
}
