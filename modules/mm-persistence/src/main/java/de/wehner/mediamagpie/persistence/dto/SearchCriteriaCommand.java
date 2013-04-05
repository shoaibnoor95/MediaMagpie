package de.wehner.mediamagpie.persistence.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.common.util.MinMaxValue;

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

    /**
     * Provides search start search period based on the value of input field which can be described as: 01.01.&lt;
     * {@linkplain #getYearStartFromInputField()}&gt;<br/>
     * If no start year is given within the {@linkplain #getSliderYearValues().getMin()} year will be used.
     * 
     * @return The start date for searching as a Date object. This will always start with 01.01.
     */
    public Date getSearchBeginAsDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(getSliderYearValues().getMin(), 0, 1, 0, 0, 0);
        if (getYearStartFromInputField() != null) {
            calendar.set(getYearStartFromInputField(), 0, 1);
        }
        return calendar.getTime();
    }

    /**
     * When an ending year is given by the input field ({@linkplain #getYearEndFromInputField()}) this method provides the end of this year.
     * When the input field has not year for ending this method provides the 31.12. of current year.
     * 
     * @return The 31.12. of search end as a Date object.
     */
    public Date getSearchEndAsDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        Integer endYear = getYearEndFromInputField();
        if (endYear == null) {
            endYear = calendar.get(Calendar.YEAR);
        }
        calendar.set(endYear, 11, 31, 23, 59, 59);
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

    public static SearchCriteriaCommand createInstance() {
        SearchCriteriaCommand searchCriteriaCommand = new SearchCriteriaCommand();
        searchCriteriaCommand.setSliderYearValues(new MinMaxValue<Integer>(0, Integer.MAX_VALUE));
        searchCriteriaCommand.setSortOrder(UiMediaSortOrder.ID);
        return searchCriteriaCommand;
    }
}
