# YetAnotherConfigLib 3.6.0

This build supports the following versions:
- Fabric 1.21.2
- Fabric 1.20.1
- Fabric 1.20.4
- Fabric 1.20.6 (also supports 1.20.5)
- Fabric 1.21
- NeoForge 1.21
- NeoForge 1.20.6 (also supports 1.20.5)
- NeoForge 1.20.4
- MinecraftForge 1.20.1

## State Managers

Options now no longer hold their state themselves. This job is now delegated to the `StateManager`.

This change serves to allow multiple options to share the same state, but with different controllers, descriptions,
names, etc.

### Example

Take an example of how this might be useful:

You have a long range of gamepad bindings, utilising a custom YACL _controller_. They are sorted alphabetically in YACL.
You want a specific gamepad binding, let's call it 'Jump', to be featured at the top of the list, but also still
appear alphabetically in the list.

You add a second 'Jump' YACL _option_ and place it at the beginning of the _category_, you assign it an identical
_binding_.

This appears to work, but you discover a problem: you can modify the featured _option_ just fine, but when you hit
'Save Changes', you see an error in your log 'Option value mismatch' and your option defaults to the previous, now
unchanged value.

Why is this? Each instance of _`Option`_ holds its own _pending value_. Which is then applied to the _binding_ when you
click save. When you click save, YACL iterates over each option in-order and applies the pending value to the _binding._
Which leads to the following series of events:

1. YACL successfully applies the featured 'Jump' option to the binding, as it's first in the list.
2. YACL then finds the non-featured 'Jump' option, which retained the original default value because the
   _pending value_ has not changed. From step 1, the binding's value has now changed to something none-default, so now
   it sets the binding again to the pending value of THIS option, now the binding is back to its default state.
3. YACL finishes saving _options_, and checks over each one again. It checks that the _pending value_ matches the
   _binding_. Because the featured 'Jump' option now doesn't match, it creates an error in your log:
   'Option value mismatch' and assigns the _pending value_ to the _binding_ value.

### Solution

State managers essentially solve this by moving the _pending value_ into a separate object that can then be given to
multiple _options_. They ensure that each option's controller is kept up to date by emitting events upon the state's
change that controllers then listen to, to keep themselves up to date.

### Code Examples

```java
StateManager<Boolean> stateManager = StateManager.createSimple(getter, setter, def);

category.option(Option.<Boolean>createOption()
        .name(Component.literal("Sharing Tick Box"))
        .stateManager(stateManager)
        .controller(TickBoxControllerBuilder::create)
        .build());
category.option(Option.<Boolean>createOption()
        .name(Component.literal("Sharing Boolean"))
        .stateManager(stateManager)
        .controller(BooleanControllerBuilder::create)
        .build());
```

Here, instead of using a `.binding()`, you use a `.stateManager()`. Now both share and update the same state! 

### Event Changes

Options that listen to events, `.listener((option, newValue) -> {})`, the syntax has slightly changed. The
aforementioned methods have been deprecated, replaced with `.addListener((option, event) -> {})`, you can retrieve the
new value with `option.pendingValue()`.

### Consolidation of .instant() behaviour

Because of this open-ness of how an option's pending value gets applied, users of YACL have a lot more power
on what happens with how an option's state is applied.

```java
.stateManager(StateManager.createInstant(getter, setter, def))
```

Is the new way to get instantly applied options. You can also implement `StateManager` yourself.

## Other Changes

- Added support for 1.21.2
- Label Options now allow change of state, so labels can now be changed dynamically.
