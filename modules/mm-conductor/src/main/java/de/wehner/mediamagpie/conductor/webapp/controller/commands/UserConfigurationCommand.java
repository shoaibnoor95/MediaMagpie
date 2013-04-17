package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.persistence.entity.PasswordConfiguration;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;

public class UserConfigurationCommand extends User implements PasswordConfiguration {

    private static final long serialVersionUID = 1L;

    private String _passwordConfirm;

    private UserConfiguration _userConfiguration;

    private boolean syncMediaPahtes;

    public UserConfigurationCommand() {
        _userConfiguration = new UserConfiguration();
    }

    public void setPasswordConfirm(String passwordConfirm) {
        _passwordConfirm = passwordConfirm;
    }

    public String getPasswordConfirm() {
        return _passwordConfirm;
    }

    public static UserConfigurationCommand createCommand(User user, UserConfiguration configuration) {
        UserConfigurationCommand command = new UserConfigurationCommand();
        command.setName(user.getName());
        command.setForename(user.getForename());
        command.setSurname(user.getSurname());
        command.setEmail(user.getEmail());
        command.setRole(user.getRole());
        command.setId(user.getId());
        command.setPassword(""/* user.getPassword() */);
        command.setPasswordConfirm(""/* user.getPassword() */);
        command.setGroups(user.getGroups());

        command.setDetailImageSize(configuration.getDetailImageSize());
        command.setRootMediaPathes(configuration.getRootMediaPathes());
        command.setThumbImageSizeTable(configuration.getThumbImageSizeTable());
        command.setThumbImageSize(configuration.getThumbImageSize());
        return command;
    }

    public String[] getRootMediaPathes() {
        return _userConfiguration.getRootMediaPathes();
    }

    public void setRootMediaPathes(String[] rootMediaPathes) {
        _userConfiguration.setRootMediaPathes(rootMediaPathes);
    }

    @Max(value = 500)
    @Min(value = 10)
    public Integer getThumbImageSize() {
        return _userConfiguration.getThumbImageSize();
    }

    public void setThumbImageSize(Integer thumbImageSize) {
        _userConfiguration.setThumbImageSize(thumbImageSize);
    }

    @Deprecated
    @Max(value = 500)
    @Min(value = 10)
    public Integer getThumbImageSizeTable() {
        return _userConfiguration.getThumbImageSizeTable();
    }

    public void setThumbImageSizeTable(Integer thumbImageSizeTable) {
        _userConfiguration.setThumbImageSizeTable(thumbImageSizeTable);
    }

    @Max(value = 2048)
    @Min(value = 10)
    public Integer getDetailImageSize() {
        return _userConfiguration.getDetailImageSize();
    }

    public void setDetailImageSize(Integer detailImageSize) {
        _userConfiguration.setDetailImageSize(detailImageSize);
    }

    public UserConfiguration getUserConfiguration() {
        return _userConfiguration;
    }

    public boolean isSyncMediaPahtes() {
        return syncMediaPahtes;
    }

    public void setSyncMediaPahtes(boolean syncMediaPahtes) {
        this.syncMediaPahtes = syncMediaPahtes;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
