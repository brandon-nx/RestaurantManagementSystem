package com.restaurantmanagementsystem.pos.controller;

import com.restaurantmanagementsystem.pos.db.MenuDao;
import com.restaurantmanagementsystem.pos.db.MenuDaoImpl;
import com.restaurantmanagementsystem.pos.model.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.List;

public class MenuController {

    @FXML
    private TabPane menuTabPane;
    private MenuDao menuDao;

    public void initialize() {
        menuDao = new MenuDaoImpl();
        loadMenuCategories();
    }

    private void loadMenuCategories() {
        String[] categories = {"Appetizers", "Entrees", "Sides", "Desserts", "Beverages"};
        for (String category : categories) {
            Tab tab = new Tab(category);
            ScrollPane scrollPane = new ScrollPane();
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            // Load the menu items into the grid
            List<MenuItem> menuItems = menuDao.getMenuItemsByCategory(category);
            int row = 0, col = 0;
            for (MenuItem item : menuItems) {
                VBox itemBox = createItemVBox(item);
                gridPane.add(itemBox, col, row);
                col++;
                if (col > 5) {
                    col = 0;
                    row++;
                }
            }
            scrollPane.setContent(gridPane);
            tab.setContent(scrollPane);
            menuTabPane.getTabs().add(tab);
        }
    }

    private VBox createItemVBox(MenuItem item) {
        VBox box = new VBox(10);
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(item.getImagePath()));
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        Text name = new Text(item.getName());
        Text price = new Text(String.format("RM%.2f", item.getPrice()));

        // Add button functionality here
        // Button addButton = new Button("Add");
        // addButton.setOnAction(event -> /* add to order functionality */);

        box.getChildren().addAll(imageView, name, price/*, addButton*/);
        return box;
    }
}
