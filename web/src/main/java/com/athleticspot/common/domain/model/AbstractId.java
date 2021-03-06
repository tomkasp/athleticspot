//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.athleticspot.common.domain.model;

import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


@MappedSuperclass
public abstract class AbstractId
    implements Identity, Serializable {

    private static final long serialVersionUID = 1L;

    @Column
    private String uuid;

    public String uuid() {
        return this.uuid;
    }

    public AbstractId(String uuid) {
        this.setUuid(uuid);
    }

    protected AbstractId() {
        super();
        this.setUuid(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractId)) return false;
        AbstractId that = (AbstractId) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [uuid=" + uuid + "]";
    }


    protected void validateId(String anId) {
        // implemented by subclasses for validation.
        // throws a runtime exception if invalid.
    }

    private void setUuid(String anId) {
        Assert.hasLength(anId, "The basic identity is required.");
        Assert.isTrue(anId.length() == 36, "The basic identity must be 36 characters.");
        this.validateId(anId);
        this.uuid = anId;
    }
}
