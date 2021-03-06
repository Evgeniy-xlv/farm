/*
 * Copyright (c) 2016-2019 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.farm.props;

import com.zerocracy.Item;
import java.io.IOException;
import java.nio.file.Path;
import lombok.EqualsAndHashCode;
import org.cactoos.Func;
import org.cactoos.Proc;
import org.cactoos.func.IoCheckedFunc;

/**
 * Props item.
 *
 * <p>This Item represents the {@code _props.xml} file in a PMO project.</p>
 *
 * @since 1.0
 */
@EqualsAndHashCode(of = "props")
final class PropsItem implements Item {

    /**
     * Temp file.
     */
    private final Path props;

    /**
     * Ctor.
     * @param props Temp file
     */
    PropsItem(final Path props) {
        this.props = props;
    }

    @Override
    public String toString() {
        return "_props.xml";
    }

    @Override
    public <T> T read(final Func<Path, T> reader) throws IOException {
        return new IoCheckedFunc<>(reader).apply(this.props);
    }

    @Override
    public void update(final Proc<Path> writer) throws IOException {
        throw new IOException("props is readonly");
    }
}
