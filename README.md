This is an example mod, demonstrating how to implement an item driver that allows placing items as components into computer cases and servers, and provide methods to the computer they are placed into.

**Important**: you'll need the OpenComputers API in your classpath to compile this example.

The example item will allow spawning particle effects. When placed in a computer's card slot, it can be accessed as a component named "particle":
```
lua> for i = 1, 100 do component.particle.spawn("flame", 0, 2, 0") end
```

Feel free to submit pull requests to expand and/or clarify documentation!