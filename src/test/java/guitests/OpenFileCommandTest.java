package guitests;

import java.io.File;

//Test for opening a new file
public class OpenFileCommandTest extends TaskManagerGuiTest {
    
    private static final String TEST_DATA_FOLDER = "src" + File.separator + "test" +
            File.separator + "data" + File.separator + "StorageDirectoryUtilTest" + File.separator;
    
    private static final String INEXISTENT_FILE = " Inexistent.xml";
    private static final String INVALID_FILE = "InvalidFormatTaskManager.xml";
    private static final String VALID_FILE = "ValidFormatTaskManager.xml";
    
}
