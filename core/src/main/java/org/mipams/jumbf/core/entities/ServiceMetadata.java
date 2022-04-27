package org.mipams.jumbf.core.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceMetadata {

    private @Getter @Setter int boxTypeId;

    private @Getter @Setter @NonNull String boxType;

}
