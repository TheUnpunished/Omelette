
package xyz.unpunished.model;

import javafx.scene.control.TitledPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jdom2.Element;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tile {

    private Element object;
    private TitledPane attachedPane;
    
}
