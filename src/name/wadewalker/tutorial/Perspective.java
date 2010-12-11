package name.wadewalker.tutorial;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

    /*
     * Puts the single JOGL view in the editor area.
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout( IPageLayout ipagelayout ) {
        ipagelayout.setEditorAreaVisible( true );
    }
}
