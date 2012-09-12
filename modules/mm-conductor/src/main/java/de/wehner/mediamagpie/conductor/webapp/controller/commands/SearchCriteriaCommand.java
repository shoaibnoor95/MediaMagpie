package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.common.util.MinMaxValue;
import de.wehner.mediamagpie.conductor.webapp.controller.media.common.UiMediaSortOrder;

public class SearchCriteriaCommand {

    public static enum Action {
        DELETE, UNKNOWN
    }

    private Action _action;
    private Long _id;
    /**
     * The slider's range scala
     */
    private MinMaxValue<Integer> _sliderYearMinMax;
    /**
     * The selected range
     */
    private MinMaxValue<Integer> _sliderYearValues;
    /**
     * The input field next to the slider containing something like '2010-2012'
     */
    private String _yearCriteria;
    private String _buzzword;
    private UiMediaSortOrder _sortOrder;
    
    public void setAction(Action action) {
        _action = action;
    }

    public Action getAction() {
        return _action;
    }

    public void setId(Long id) {
        _id = id;
    }

    public Long getId() {
        return _id;
    }

    public MinMaxValue<Integer> getSliderYearMinMax() {
        return _sliderYearMinMax;
    }

    public void setSliderYearMinMax(MinMaxValue<Integer> sliderYearMinMax) {
        _sliderYearMinMax = sliderYearMinMax;
    }

    public MinMaxValue<Integer> getSliderYearValues() {
        return _sliderYearValues;
    }

    public void setSliderYearValues(MinMaxValue<Integer> sliderYearValues) {
        _sliderYearValues = sliderYearValues;
    }

    public String getYearCriteria() {
        return _yearCriteria;
    }

    public void setYearCriteria(String yearCriteria) {
        _yearCriteria = yearCriteria;
    }

    public String getBuzzword() {
        return _buzzword;
    }

    public void setBuzzword(String buzzword) {
        _buzzword = buzzword;
    }

    public UiMediaSortOrder getSortOrder() {
        return _sortOrder;
    }

    public void setSortOrder(UiMediaSortOrder sortOrder) {
        _sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public Date getYearStartFromInputFieldAsDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(1900, 0, 1, 0, 0, 0);
        if (getYearStartFromInputField() != null) {
            calendar.set(getYearStartFromInputField(), 0, 1);
        }
        return calendar.getTime();
    }

    public Date getYearEndFromInputFieldAsDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        if (getYearEndFromInputField() != null) {
            calendar.set(getYearEndFromInputField(), 11, 31, 23, 59, 59);
        }
        return calendar.getTime();
    }

    public Integer getYearStartFromInputField() {
        if (StringUtils.isEmpty(_yearCriteria)) {
            return null;
        }
        String[] years = StringUtils.split(_yearCriteria, '-');
        return Integer.parseInt(years[0].trim());
    }

    public Integer getYearEndFromInputField() {
        if (StringUtils.isEmpty(_yearCriteria)) {
            return null;
        }
        String[] years = StringUtils.split(_yearCriteria, '-');
        if (years.length == 2) {
            return Integer.parseInt(years[1].trim());
        } else if (years.length == 1) {
            return Integer.parseInt(years[0].trim());
        }
        return null;
    }
}
