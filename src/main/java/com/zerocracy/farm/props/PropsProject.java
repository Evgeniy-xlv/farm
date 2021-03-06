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
import com.zerocracy.Project;
import java.io.IOException;
import java.nio.file.Path;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoCheckedScalar;

/**
 * Props project.
 *
 * <p>A project which acquires its {@code _props.xml} file and also
 * adds some post processing directives to it.</p>
 *
 * @since 1.0
 */
@EqualsAndHashCode(of = "origin")
final class PropsProject implements Project {

    /**
     * Origin project.
     */
    private final Project origin;

    /**
     * Props file.
     */
    private final Scalar<Path> props;

    /**
     * Ctor.
     * @param pkt Project
     * @param props Properties file
     */
    PropsProject(final Project pkt, final Scalar<Path> props) {
        this.origin = pkt;
        this.props = props;
    }

    @Override
    public String pid() throws IOException {
        return this.origin.pid();
    }

    @Override
    public Item acq(final String file) throws IOException {
        final Item item;
        if ("_props.xml".equals(file)) {
            item = new PropsItem(new IoCheckedScalar<>(this.props).value());
        } else {
            item = this.origin.acq(file);
        }
        return item;
    }
}
