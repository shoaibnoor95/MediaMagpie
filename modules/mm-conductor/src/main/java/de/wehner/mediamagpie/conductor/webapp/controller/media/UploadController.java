package de.wehner.mediamagpie.conductor.webapp.controller.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import de.wehner.mediamagpie.aws.s3.service.S3SyncService;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.FileUploadCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.json.JQueryUploadCommand;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

/**
 * For more information about used jQuery Plugin, please refer to: <code>http://aquantum-demo.appspot.com/file-upload</code>
 * 
 * @author ralfwehner
 */
@Controller
@RequestMapping("/upload")
public class UploadController extends AbstractConfigurationSupportController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    public static final String URL_UPLOAD = "/file-upload";
    public static final String VIEW_UPLOAD = "upload/upload";

    public static final String URL_DELETE_FILE = "/file-delete";

    private final UploadService _uploadService;

    private final S3SyncService _s3SyncService;

    private final PersistenceService _persistenceService;

    @Autowired
    public UploadController(ConfigurationProvider configurationProvider, UploadService uploadService, S3SyncService s3SyncService, PersistenceService persistenceService) {
        super(configurationProvider, null);
        _uploadService = uploadService;
        _s3SyncService = s3SyncService;
        _persistenceService = persistenceService;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_UPLOAD)
    public String showUpload(Model model) {
        model.addAttribute("fileUploadCommand", new FileUploadCommand());
        return VIEW_UPLOAD;
    }

    /**
     * Great article explaining the file upload via drag and drop and XHR (XMLHttpRequest):
     * <code>http://robertnyman.com/2010/12/16/utilizing-the-html5-file-api-to-choose-upload-preview-and-see-progress-for-multiple-files/#comment-682330</code>
     * <br/>
     * Used this now: <code>http://aquantum-demo.appspot.com/file-upload</code>
     * 
     */
    @RequestMapping(method = RequestMethod.POST, value = URL_UPLOAD)
    public @ResponseBody
    List<JQueryUploadCommand> doPost(HttpServletRequest request) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }

        MultipartRequest multipartRequst = (DefaultMultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequst.getFileMap();
        Set<Entry<String, MultipartFile>> entrySet = fileMap.entrySet();
        List<JQueryUploadCommand> jQueryUploadCommands = new ArrayList<JQueryUploadCommand>();
        for (Entry<String, MultipartFile> entry : entrySet) {
            MultipartFile multipartFile = entry.getValue();
            LOG.debug("Receive new upload file '" + multipartFile.getOriginalFilename() + "'.");
            if (StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
                throw new RuntimeException("Internal error: No file name given");
            }

            // upload file, create Media, Thumb, thumbimage link etc..
            User currentUser = SecurityUtil.getCurrentUser();
            Pair<String, File> uploadFileInfo = _uploadService.createUniqueUserStoreFile(currentUser, multipartFile.getOriginalFilename());
            LOG.info("Try dump upload stream '" + uploadFileInfo.getFirst() + "' into file '" + uploadFileInfo.getSecond().getPath() + "'");
            Media newMedia = _uploadService.saveInputStreamToFileSystemAndCreateMedia(currentUser, uploadFileInfo.getSecond(),
                    multipartFile.getInputStream());
            _persistenceService.persist(newMedia);

            // create job executions for a) image resizing and S3 Upload
            S3Configuration userS3Configuration = getCurrentUsersS3Configuration();
            if (_s3SyncService != null && userS3Configuration.hasToSyncToS3()) {
                // sync media to s3 bucket
                _s3SyncService.pushToS3(newMedia);
            }
            _uploadService.createJobsForAllThumbImages(newMedia, _configurationProvider);

            // create a thumb image for the upload view
            String contextPath = request.getContextPath();
            String thumbUrl = contextPath
                    + _uploadService.createThumbImage(newMedia, de.wehner.mediamagpie.conductor.webapp.services.UploadService.UPLOAD_PREVIEW_THUMB_LABEL,
                            Priority.HIGH, 2000);

            // build propper command for respose
            JQueryUploadCommand command = new JQueryUploadCommand(multipartFile.getOriginalFilename(), (int) multipartFile.getSize(), "url", thumbUrl,
                    contextPath + getDeleteUrl(uploadFileInfo.getSecond().getName()), "DELETE");
            jQueryUploadCommands.add(command);
        }
        return jQueryUploadCommands;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = URL_DELETE_FILE)
    public void deleteFile(@RequestParam("file") String relStoreFile, HttpServletResponse response) {
        _uploadService.deleteFile(SecurityUtil.getCurrentUser(), relStoreFile);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static String getUploadUrl() {
        return UploadController.class.getAnnotation(RequestMapping.class).value()[0] + URL_UPLOAD;
    }

    public static String getDeleteUrl(String relStoreFile) {
        return UploadController.class.getAnnotation(RequestMapping.class).value()[0] + URL_DELETE_FILE + "?file=" + relStoreFile;
    }
}
