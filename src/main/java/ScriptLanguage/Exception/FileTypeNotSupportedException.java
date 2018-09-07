package ScriptLanguage.Exception;

public class FileTypeNotSupportedException extends RunTimeException{
    private String fileType;

    public FileTypeNotSupportedException(String fileType) {
        this.fileType = fileType;
    }

    public String toString(){
        return "File type " + fileType + " not supported";
    }
}
