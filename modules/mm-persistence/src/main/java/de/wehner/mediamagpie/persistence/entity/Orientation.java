package de.wehner.mediamagpie.persistence.entity;

/**
 * This enumeration describes the orientation of the origin media, especially a photo. It is derived from the EXIF tag 'orientation'. For
 * more information please have a look to: http://sylvana.net/jpegcrop/exif_orientation.html
 * 
 * @author ralfwehner
 * 
 */
public enum Orientation {

    UNKNOWN(0),

    /**
     * 1, The 'normal' orientation were no rotation is required
     * <p>
     * <b>The normal case</b>
     * </p>
     */
    TOP_LEFT_SIDE(0),

    /**
     * 2, Top, but we need to mirroring vertically
     */
    TOP_RIGHT_SIDE(0),

    /**
     * 3, Bottom and need rotation to 180 degree
     */
    BOTTOM_RIGHT_SIDE(180),

    /**
     * 4, Bottom and need rotation to 180 degree and mirrorint vertically
     */
    BOTTOM_LEFT_SIDE(180),

    /**
     * 5, need rotation of 90 degree and mirroring horizontally
     */
    LEFT_SIDE_TOP(90),

    /**
     * 6, need rotation of 90 degree - "Right side, top (Rotate 90 CW)" (um 90 Grad gegen den Uhrzeigersinn gedreht)
     * <p>
     * <b>The normal case</b>
     * </p>
     */
    RIGHT_SIDE_TOP(90),

    /**
     * 7, need rotation of -90 degree and mirroring vertically
     */
    RIGHT_SIDE_BOTTOM(-90),

    /**
     * 8, need rotation of -90 -  "Left side, bottom (Rotate 270 CW)"; (um 90 Grad gegen im Uhrzeigersinn gedreht)
     * <p>
     * <b>The normal case</b>
     * </p>
     */
    LEFT_SIDE_BOTTOM(-90);

    private final int _necessaryRotation;

    private Orientation(int necessaryRotation) {
        _necessaryRotation = necessaryRotation;
    }

    public int getNecessaryRotation() {
        return _necessaryRotation;
    }
}
