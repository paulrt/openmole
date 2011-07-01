/*
 * Copyright (C) 2011 leclaire
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openmole.ide.core;

import java.awt.BorderLayout;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.logging.Logger;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openmole.ide.core.display.Displays;
/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.openmole.ide.core//EntityProperty//EN",
autostore = false)
public final class EntityPropertyTopComponent extends TopComponent {

    private static EntityPropertyTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "EntityPropertyTopComponent";

    public EntityPropertyTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(EntityPropertyTopComponent.class, "CTL_EntityPropertyTopComponent"));
        setToolTipText(NbBundle.getMessage(EntityPropertyTopComponent.class, "HINT_EntityPropertyTopComponent"));
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

        setLayout(new BorderLayout());

      //  propertyPanel.setDropTarget(new DropTarget(propertyPanel, new PanelDropTarget()));
      //  add(PropertyPanel.getDefault(), BorderLayout.CENTER);
        add(Displays.propertyPanel().peer(), BorderLayout.CENTER);

//        
//        DropTarget dt = new DropTarget(this, new PropertyDropTargetListener());
//        dt.setDefaultActions(DnDConstants.ACTION_MOVE);
//        dt.setActive(true);
//        setDropTarget(dt);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized EntityPropertyTopComponent getDefault() {
        if (instance == null) {
            instance = new EntityPropertyTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the EntityPropertyTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized EntityPropertyTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(EntityPropertyTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof EntityPropertyTopComponent) {
            return (EntityPropertyTopComponent) win;
        }
        Logger.getLogger(EntityPropertyTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public class PanelDropTarget implements DropTargetListener {

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            System.out.println("DROPE !!!");
        }
    }
}
