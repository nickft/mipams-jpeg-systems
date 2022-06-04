package org.mipams.jumbf.core.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ParseMetadata {
    private @Getter @Setter long availableBytesForBox;
    private @Getter @Setter String parentDirectory;
}
